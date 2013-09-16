package com.paypal.asgard

import com.paypal.asgard.auth.UserLoginToken
import com.paypal.asgard.exception.RestClientRequestException
import grails.converters.JSON
import grails.converters.XML
import org.apache.shiro.SecurityUtils
import org.apache.shiro.grails.ConfigUtils
import org.apache.shiro.web.util.WebUtils

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

class AuthController {

    def static allowedMethods = [signIn: 'POST', signOut: 'GET']

    def shiroSecurityManager
    def openStackRESTService
    def sessionStorageService
    def configService

    def index = {
        redirect(action: "login", params: params)
    }

    def login = {
        if (grailsApplication.metadata['grails.env'] != 'production'){
            configService.reloadConfig()
            if (!configService.appConfigured) {
                redirect(controller: 'init')
                return
            }
        }
        if (SecurityUtils.subject?.isAuthenticated()) {
            redirect(uri: "/")
            return true
        }

        params.environment = g.cookie(name: "environment")
        return [username: params.username, rememberMe: (params.rememberMe != null), targetUri: params.targetUri]
    }

    def attemptToConnect = {
        if (SecurityUtils.subject?.isAuthenticated()) {
            redirect(uri: "/")
            return true
        }

        //tenant
        def userLoginToken = new UserLoginToken((String)params.username, (String)params.password, (String)params.environment)

        // Support for "remember me"
        if (params.rememberMe) {
            userLoginToken.rememberMe = true
        }

        // If a controller redirected to this page, redirect back
        // to it. Otherwise redirect to the root URI.
        def targetUri = params.targetUri ?: "/"

        // Handle requests saved by Shiro filters.
        def savedRequest = WebUtils.getSavedRequest(request)
        if (savedRequest) {
            targetUri = savedRequest.requestURI - request.contextPath
            if (savedRequest.queryString) targetUri = targetUri + '?' + savedRequest.queryString
        }

        SecurityUtils.subject.login(userLoginToken)
    }

    def getRedirectUrl() {
        def environmentName = params.environment
        def environment = grailsApplication.config.properties.environments.find {it.name == environmentName}

        environment?.redirect_url
    }

    def signIn = { ConnectionCommand cmd ->
        if (cmd.hasErrors()){
            def model = cmd.errors
            withFormat {
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
            return false
        }

        def redirect_url = redirectUrl

        if (redirect_url) {
            def listErrors = ['redirectUrl': redirect_url]
            def errors = listErrors
            def model = [errors : errors]
            withFormat {
                html { model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
            return true
        }

        attemptToConnect()

        try{
            def dataCenterMap = openStackRESTService.sessionStorageService.getDataCentersMap()
            def listErrors = [:]
            def possibleToConnect = openStackRESTService.haveAvailableDatacenters
            def authErrors = openStackRESTService.authErrors
            def environmentError = openStackRESTService.environmentError
            for (datacenter in dataCenterMap){
                if (datacenter.value.error == null)
                    continue
                def error =  datacenter.value.error
                String name = datacenter.value.name
                if (error){
                    name = name.substring(1,name.length());
                    listErrors[name] = error;
                }
            }

            def code = null

            if (possibleToConnect){
                code = 200
            }else{
                if (authErrors || environmentError) {
                    code = 401
                }
                else {
                    code = 500
                }
            }

            if (code == 200) {
                response.addCookie(new Cookie("environment", params.environment))
            }

            response.status = code
            if (environmentError)
                listErrors['environmentError'] = true

            def model = [errors : listErrors]

            withFormat {
                html { model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = openStackRESTService.getCode(e)
            withFormat {
                html { flash.message = error; redirect(uri: "/")}
                xml { new XML([errors : error]).render(response) }
                json { new JSON([errors : error]).render(response)}
            }
        }
    }

    def signOut = {
        // Log the user out of the application.
        try{
            sessionStorageService.clearSession()
            def principal = SecurityUtils.subject?.principal
            SecurityUtils.subject?.logout()
            // For now, redirect back to the home page.
            if (ConfigUtils.getCasEnable() && ConfigUtils.isFromCas(principal)) {
                redirect(uri: ConfigUtils.getLogoutUrl())
            } else {
                redirect(uri: "/")
            }
            ConfigUtils.removePrincipal(principal)
        } catch (Exception e){
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                html { redirect(uri: "/") }
                xml { new XML([errors : error]).render(response)}
                json { new JSON([errors : error]).render(response)}
            }
        }
    }

    def unauthorized = {
        render "You do not have permission to access this page."
    }

    class ConnectionCommand{
        String username
        String password
        String environment
        String targetUri
        boolean rememberMe
        static constraints = {
            username(nullable: false, blank: false)
            password(nullable: false, blank: false)
            environment(nullable: false, blank: false)
        }
    }

    def notSupported(){
        response.sendError(405)
    }
}
