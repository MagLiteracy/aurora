package com.paypal.aurora

import com.paypal.aurora.model.Tenant

class TenantService {

    private static final String TENANTS = 'tenants'
    public static final String KEYSTONE_CUSTOM_TENANCY = 'keystone_custom_tenancy'

    def openStackRESTService
    def sessionStorageService

    def getAllTenants() {
        def resp = openStackRESTService.get(openStackRESTService.KEYSTONE, "${TENANTS}")
        def tenants = []
        for (tenant in resp.tenants) {
            tenants << new Tenant(tenant)
        }
        tenants
    }

    Map<String, Tenant> getTenantsMap() {
        Map<String, Tenant> result = [:]
        allTenants.each {
            result.put(it.id, it)
        }
        result
    }

    def createTenant(def params) {
        def body = ["tenant": ["name": params.name, "description": params.description, "enabled": params.enabled == 'on']]
        if (params.zones) {
            body.tenant.zones = params.zones.split('\n')
        }
        def resp = openStackRESTService.post(openStackRESTService.KEYSTONE, TENANTS, body)
        new Tenant(resp.tenant)
    }

    def updateTenant(def params) {
        def body = ["tenant": ["id": params.id, "name": params.name, "description": params.description, "enabled": params.enabled == 'on']]
        if (params.zones) {
            body.tenant.zones = params.zones.split('\n')
        }
        def resp = openStackRESTService.post(openStackRESTService.KEYSTONE, "${TENANTS}/${params.id}", body)
        new Tenant(resp.tenant)
    }

    def getTenantById(def tenantId) {
        def resp = openStackRESTService.get(openStackRESTService.KEYSTONE, "${TENANTS}/${tenantId}")
        new Tenant(resp.tenant)
    }

    def getTenantByName(def tenantName) {
        def tenants = getAllTenants()
        tenants.find {it.name == tenantName}
    }

    def deleteTenantById(def tenantId) {
        openStackRESTService.delete(openStackRESTService.KEYSTONE, "${TENANTS}/${tenantId}")
    }

    boolean isKeystoneCustomTenancy() {
        sessionStorageService.isFlagEnabled(KEYSTONE_CUSTOM_TENANCY)
    }
}
