package com.paypal.aurora

import com.paypal.aurora.auth.UserLoginToken
import com.paypal.aurora.exception.LbaasException
import com.paypal.aurora.exception.RestClientRequestException
import com.paypal.aurora.model.OpenStackService
import com.paypal.aurora.model.Tenant
import com.paypal.aurora.model.UserState
import groovyx.net.http.ParserRegistry
import groovyx.net.http.RESTClient
import org.apache.http.HttpResponse
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.entity.ContentType
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

class OpenStackRESTService {

    static transactional = false
    static final String NOVA = 'compute'
    static final String NOVA_VOLUME = 'volume'
    static final String GLANCE = 'image'
    static final String LBMS = 'lbms'
    static final String KEYSTONE = 'identity'
    static final String HEAT = 'orchestration'
    static final String QUANTUM = 'network'
    static final String PSERV = 'pserv'
    static final String DNS = 'dns'
    static final String TEXT_PLAIN = 'text/plain'
    static final String APPLICATION_JSON = 'application/json'
    static final def ADDITIONAL_HEADERS = [(NOVA_VOLUME): ['User-Agent': 'python-cinderclient']]
    static final def CONTENT_TYPE = [(TEXT_PLAIN): ContentType.TEXT_PLAIN]
    static final def RESPONSE_PARSER = [
            (APPLICATION_JSON): [
                    (TEXT_PLAIN): { HttpResponse resp ->
                        ParserRegistry parser = new ParserRegistry()
                        resp.setHeader('Content-Type', 'text/plain')
                        BufferedReader convert = new BufferedReader(parser.parseText(resp))
                        def result = [str: convert.readLine()]
                        return result
                    }
            ]
    ]

    def grailsApplication

    def haveAvailableDatacenters
    def environmentError
    def authErrors

    def sessionStorageService

    def getCode(Exception e) {
        def code
        try {
            code = e.statusCode;
        } catch (Exception er) {
            code = null;
        }
        return code;
    }

    void setProxy(def proxy) {
        if (proxy) {
            String[] split = proxy.split(':')
            System.setProperty('socksProxySet', 'true')
            System.setProperty('socksProxyHost', split[0])
            System.setProperty('socksProxyPort', split[1])
        } else {
            System.setProperty('socksProxySet', 'false')
            System.setProperty('socksProxyHost', '')
            System.setProperty('socksProxyPort', '')
        }
    }

    void login(UserLoginToken userLoginToken) {
        def environment = grailsApplication.config.properties.environments.find {
            it.name == userLoginToken.environment
        }

        environmentError = false

        if (!environment) {
            haveAvailableDatacenters = false
            environmentError = true
            return
        }

        sessionStorageService.setCurrentEnv(environment)
        sessionStorageService.setCustomServices(environment.datacenters.customservices)
        sessionStorageService.setUser(userLoginToken.username)
        log.info("Environment = ${environment}")
        def datacenters = environment.get("datacenters")
        if (!datacenters) {
            haveAvailableDatacenters = false
            environmentError = true
            return
        }
        def updateSessionAttributes = true
        authErrors = false

        datacenters.each() {
            setProxy(it.proxy)
            RESTClient client = new RESTClient(it.keystone)

            def tokenId

            it.error = null


            try {
                tokenId = getTokenId(client, userLoginToken.principal, userLoginToken.credentials.toString())
            } catch (Exception e) {
                def errorMessage = e.getMessage()
                if (e instanceof UnknownHostException)
                    errorMessage = 'Unknown host ' + errorMessage
                if (it.name[0] != '?')
                    it.name = "?" + it.name
                it.error = errorMessage
                def code = getCode(e);
                it.statusCode = code
                if (code == 401)
                    authErrors = true

                log.info(e)
            }

            sessionStorageService.setDataCenter(it.name, it)

            if (!tokenId) {
                return
            }


            client.setHeaders('X-Auth-Token': tokenId)

            it.tokenId = tokenId

            if (updateSessionAttributes) {
                client.setHeaders('X-Auth-Token': tokenId)
                def tenants = collectTenants(client)
                sessionStorageService.setTenants(tenants)
                updateServices(client, it, findDefaultTenantId(userLoginToken.username), updateSessionAttributes)
                updateSessionAttributes = false
            }
        }

        def goodDataCenter = sessionStorageService.getDataCentersMap().values()
                .find { it.error == null }

        if (goodDataCenter) {
            setProxy(goodDataCenter.proxy)
            haveAvailableDatacenters = true
        } else {
            haveAvailableDatacenters = false
        }

    }

    def private findDefaultTenantId(String userName) {
        def tenants = sessionStorageService.tenants
        def tenant = tenants.find {
            it.name == userName
        }
        return tenant ? tenant.id : tenants.last().id
    }

    def private static getTokenId(RESTClient client, def user, def password) {
        def body = ['auth': ['passwordCredentials': ['username': user, 'password': password]]]
        def resp = client.post(path: 'tokens', body: body, requestContentType: ContentType.APPLICATION_JSON.mimeType)

        resp.data.access.token.id
    }

    def private updateTenants(RESTClient client, def tokenId) {
        client.setHeaders('X-Auth-Token': tokenId)
        sessionStorageService.setTenants(collectTenants(client))
    }

    def private static collectTenants(RESTClient client) {
        def resp = client.get(path: 'tenants')

        def tenants = []
        for (tenant in resp.data.tenants) {
            tenants << new Tenant(tenant)
        }
        tenants
    }

    def private updateServices(RESTClient client, def dataCenter, def tenantId, def updateSessionAttributes) {
        def body = ['auth': ['token': ['id': dataCenter.tokenId], 'tenantId': tenantId]]
        def resp = client.post(path: 'tokens', body: body, requestContentType: ContentType.APPLICATION_JSON.mimeType)

        dataCenter.tokenId = resp.data.access.token.id

        if (updateSessionAttributes) {
            def services = [:]

            for (service in resp.data.access.serviceCatalog) {
                services.put(service.type, new OpenStackService(service.name, service.type, service.endpoints[0].publicURL, service.endpoints[0].adminURL))
            }

            dataCenter.customservices.each {
                String adminUri = it.adminUri ?: it.uri
                services.put(it.type, new OpenStackService(it.name, it.type, it.uri, adminUri, it.user, it.password, it.tenant, it.disabled ? true : false))
            }

            sessionStorageService.setTokenId(resp.data.access.token.id)
            sessionStorageService.setTenant(resp.data.access.token.tenant)
            sessionStorageService.setDataCenterName(dataCenter.name)
            sessionStorageService.setServices(services)
            sessionStorageService.setRoles(resp.data.access.user.roles.name)
        }
    }

    def private disableVerify(RESTClient client) {
        SSLContext ctx = SSLContext.getInstance('TLS');
        X509TrustManager tm = new X509TrustManager() {
            @Override
            void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }


            @Override
            void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            X509Certificate[] getAcceptedIssuers() {
                return []
            }
        };
        ctx.init(null, [tm].toArray(new TrustManager[1]), null);
        SSLSocketFactory ssf = new SSLSocketFactory(ctx);
        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
        client.client.connectionManager.schemeRegistry.register(new Scheme('https', ssf, 443))
    }

    def changeUserState(def dataCenterName, def tenantId) {

        def dataCenter = sessionStorageService.getDataCenter(dataCenterName)

        if (!dataCenter.tokenId) {
            def message = datacenterError[dataCenterName]
            def String name = dataCenterName
            throw new AuthenticationException("Cannot access ${name} because ${message}")
        }

        RESTClient client = new RESTClient(dataCenter.keystone)

        if (dataCenterName != sessionStorageService.dataCenterName) {
            setProxy(dataCenter.proxy)
            updateTenants(client, dataCenter.tokenId)
            updateServices(client, dataCenter, sessionStorageService.tenants.last().id, true)
        } else {
            updateServices(client, dataCenter, tenantId, true)
            updateTenants(client, sessionStorageService.getTokenId())
        }
        new UserState(sessionStorageService.dataCenterName, sessionStorageService.tenant.id)
    }

    def getContentType(def contentType) {
        if (contentType && contentType in CONTENT_TYPE.keySet())
            return CONTENT_TYPE[contentType]
        return ContentType.APPLICATION_JSON.mimeType
    }

    def makeClient(def host, def customParser) {
        RESTClient client = new RESTClient(host + '/')
        if (customParser) {
            def header = customParser.header
            def body = customParser.body
            def responseParser = RESPONSE_PARSER[header]?.getAt(body)
            if (responseParser)
                client.parser."${header}" = responseParser
        }
        disableVerify(client)
        return client
    }

    def private performRequest(String serviceName, String path, String request, def body = null, Map tokens = null, String returnValue = 'data', def query = null, def contentType = null, def customParser = null) {
        if (!isServiceEnabled(serviceName)) {
            return [:]
        }

        def service = sessionStorageService.services[serviceName]

        def authToken = null
        if (service.tokenId) {
            authToken = service.tokenId
        } else {
            if (service.user) {
                authToken = getTokenId(service)
            }
        }

        String host = service.publicURL
        if (SecurityUtils.subject.hasRole(Constant.ROLE_ADMIN) && service.adminURL) {
            host = service.adminURL
        }

        RESTClient client = makeClient(host, customParser)

        if (!authToken) {
            authToken = sessionStorageService.getTokenId()
        }
        service.tokenId = authToken
        def headers = ['X-Auth-Token': authToken]

        if (ADDITIONAL_HEADERS.get(serviceName)) {
            headers.putAll(ADDITIONAL_HEADERS.get(serviceName))
        }
        if (tokens) {
            headers.putAll(tokens)
        }
        client.setHeaders(headers)

        log.info("Performing '${request.toUpperCase()}' request to service '${service.type}': '${host.endsWith('/') ? host : host + '/' }${path}'")
        log.info("${headers}")
        try {
            def requestContentType = getContentType(contentType)
            def resp = client."${request}"(path: path, requestContentType: requestContentType, body: body, query: query)
            resp."${returnValue}" ?: [:]
        } catch (Exception e) {
            throw new RestClientRequestException(e.getMessage() + " '" + host + path + "'", e)
        }
    }

    def getTokenId(def service) {
        String host = sessionStorageService.getDataCenter(sessionStorageService.getDataCenterName()).keystone
        RESTClient client = new RESTClient(host + '/')

        def tokenId = getTokenId(client, service.user, service.password)

        client.setHeaders('X-Auth-Token': tokenId)
        def tenants = collectTenants(client)

        def tenantName = service.tenant ?: service.name

        def tenant = tenants.find { it.name == tenantName }

        if (tenant == null) {
            throw new LbaasException("LBAAS: Tenant id not found during authentication")
        }

        def body = ['auth': ['project': 'lbaas',
                'passwordCredentials': [
                        'username': service.user,
                        'password': service.password],
                'tenantId': tenant.id]]
        def resp = client.post(path: 'tokens', body: body, requestContentType: ContentType.APPLICATION_JSON.mimeType)
        resp.data.access.token.id
    }

    def get(String service, String path, def query = null, def contentType = null, def customParser = null) {
        performRequest(service, path, 'get', null, null, 'data', query, contentType, customParser)
    }

    def delete(String service, String path, def contentType = null, def customParser = null) {
        performRequest(service, path, 'delete', null, null, 'data', null, contentType, customParser)
    }

    def post(String service, String path, def body, Map tokens = null, def contentType = null, def customParser = null) {
        performRequest(service, path, 'post', body, tokens, 'data', null, contentType, customParser)
    }

    def put(String service, String path, Map tokens, def body = null, def contentType = null, def customParser = null) {
        performRequest(service, path, 'put', body, tokens, 'data', null, contentType, customParser)
    }

    def head(String service, String path, def contentType = null, def customParser = null) {
        performRequest(service, path, 'head', null, null, 'allHeaders', null, contentType, customParser)
    }

    boolean isServiceEnabled(String name) {
        def services = sessionStorageService.services
        services[name] && !services[name].disabled
    }

}