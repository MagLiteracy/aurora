package com.paypal.aurora

import com.google.common.collect.MapMaker
import com.paypal.aurora.model.Tenant
import org.springframework.web.context.request.RequestContextHolder

import javax.servlet.http.HttpSession
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit

//may be needed use - static scope = "session" and save data in bean
class SessionStorageService {

    static transactional = false

    private static final String TOKEN_ID = "tokenId"
    private static final String TENANT = "tenant"
    private static final String TENANTS = "tenants"
    private static final String ROLES = "roles"
    private static final String SERVICES = "services"
    private static final String CUSTOM_SERVICES = "customServices"
    private static final String CURRENT_ENVIRONMENT = "environment"
    private static final String DATA_CENTER_NAME = "dataCenterName"
    private static final String DATA_CENTERS_MAP = "dataCentersMap"
    private static final String USER = "user"
    private static final ConcurrentMap<Integer, Object> expiringMap = new MapMaker().expireAfterWrite(10, TimeUnit.MINUTES).makeMap()
    private static final Random random = new Random()

    def grailsApplication

    Integer putExpiringVar(Object obj) {
        synchronized (expiringMap) {
            while (true) {
                int rnd = random.nextInt()
                if (expiringMap.get(rnd) == null) {
                    expiringMap.put(rnd, obj)
                    return rnd
                }
            }
        }
    }

    Object getExpiringVar(Integer ind) {
        expiringMap.get(ind)
    }

    def setUser(def user) {
        getSession().setAttribute(USER, user)
    }

    def getUser() {
        getSession().getAttribute(USER)
    }

    def setTokenId(def tokenId) {
        getSession().setAttribute(TOKEN_ID, tokenId)
    }

    def setTenant(def data) {
        def tenant = new Tenant(data)
        getSession().setAttribute(TENANT, tenant)
    }

    def setTenants(def tenants) {
        getSession().setAttribute(TENANTS, tenants)
    }

    def getTokenId() {
        getSession().getAttribute(TOKEN_ID)
    }

    def getTenant() {
        getSession().getAttribute(TENANT)
    }

    def getTenants() {
        getSession().getAttribute(TENANTS)
    }

    def setRoles(def roles) {
        getSession().setAttribute(ROLES, roles)
    }

    def getRoles() {
        getSession().getAttribute(ROLES)
    }

    def setServices(def services) {
        getSession().setAttribute(SERVICES, services)
    }

    def getServices() {
        getSession().getAttribute(SERVICES)
    }

    def setCustomServices(def customServices) {
        getSession().setAttribute(CUSTOM_SERVICES, customServices)
    }

    def getCustomServices() {
        getSession().getAttribute(CUSTOM_SERVICES)
    }

    def setDataCenterName(def dataCenterName) {
        getSession().setAttribute(DATA_CENTER_NAME, dataCenterName)
    }

    //using in main.gsp
    def getDataCenterName() {
        getSession().getAttribute(DATA_CENTER_NAME)
    }

    def setDataCenter(def dataCenterName, def dataCenter) {
        def dateCentersMap = getSession().getAttribute(DATA_CENTERS_MAP)
        if (!dateCentersMap) {
            dateCentersMap = [:]
            getSession().setAttribute(DATA_CENTERS_MAP, dateCentersMap)
        }
        dateCentersMap.put(dataCenterName, dataCenter)
    }

    def getDataCenter(def dataCenterName) {
        getDataCentersMap().get(dataCenterName)
    }

    def getCurrentDatacenter() {
        getDataCenter(getDataCenterName())
    }

    def setCurrentEnv(def environment) {
        getSession().setAttribute(CURRENT_ENVIRONMENT, environment)
    }

    def getCurrentEnv() {
        getSession().getAttribute(CURRENT_ENVIRONMENT)
    }

    def getUserLoginHint() {
        getCurrentDatacenter().user_login_hint?:'Using USER Credentials:'
    }

    def getAdminLoginHint() {
        getCurrentDatacenter().admin_login_hint?:'Using ROOT Credentials:'
    }

    def clearSession(){
        //dataCentersMap?.clear()
        grailsApplication.config.properties.environments.each(){
            for (datacenter in it.get("datacenters")){
                if (datacenter.error && datacenter.name[0] == '?'){
                    datacenter.error = null
                    def len = datacenter.name.length()
                    datacenter.name = datacenter.name.substring(1,len)
                }
            }
        }
    }

    boolean isFlagEnabled(String flag) {
        def flags = getCurrentDatacenter().flags
        if (flags) {
            return flags.contains(flag)
        } else {
            return false;
        }
    }

    //using in main.gsp
    def getDataCentersMap() {
        getSession().getAttribute(DATA_CENTERS_MAP)
    }

    private HttpSession getSession() {
        return RequestContextHolder.currentRequestAttributes().getSession()
    }
}
