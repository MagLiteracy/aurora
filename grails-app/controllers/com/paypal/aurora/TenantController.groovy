package com.paypal.aurora

import com.paypal.aurora.exception.RestClientRequestException
import com.paypal.aurora.model.OpenStackUser
import com.paypal.aurora.model.Quota
import com.paypal.aurora.util.ConstraintsProcessor
import grails.converters.JSON
import grails.converters.XML
import org.apache.shiro.grails.annotations.RoleRequired

import javax.servlet.http.HttpServletResponse

class TenantController {
    final private static String MANY_USERS = "many_users"
    def static allowedMethods = [list: ['GET', 'POST'], show: ['GET', 'POST'], save: ['GET', 'POST'], update: ['GET', 'POST'], delete: ['GET', 'POST'], quotas: ['GET', 'POST'], saveQuotas: ['GET', 'POST'], users: 'GET', usersSave: ['GET', 'POST']]

    def tenantService
    def quotaService
    def openStackUserService
    def sessionStorageService

    @RoleRequired('admin')
    def index = { redirect(action: 'list', params: params) }

    @RoleRequired('admin')
    def list = {
        try {
            def tenants = tenantService.getAllTenants()
            def model = [tenants: tenants]
            withFormat {
                html { [tenants: tenants] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [tenants: [], errors: error, flash: [message: error]]
            withFormat {
                html { model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }
    }

    @RoleRequired('admin')
    def show = {
        try {
            def tenant = tenantService.getTenantById(params.id)
            Quota[] quotas = quotaService.getQuotasByTenantId(tenant.id)
            OpenStackUser[] users = openStackUserService.getAllUsersByTenant(tenant.id)
            withFormat {
                html { [parent: "/tenant/list", tenant: tenant, quotas: quotas, users: users] }
                xml { new XML([tenant: tenant, quotas: quotas, users: users]).render(response) }
                json { new JSON([tenant: tenant, quotas: quotas, users: users]).render(response) }
            }
        } catch (RestClientRequestException e) {
            def errors = ExceptionUtils.getExceptionMessage(e)
            withFormat {
                html { flash.message = errors; redirect(action: 'list') }
                xml { new XML([errors: errors]).render(response) }
                json { new JSON([errors: errors]).render(response) }
            }
        }
    }

    @RoleRequired('admin')
    def edit = {
        try {
            def tenant = tenantService.getTenantById(params.id)
            if (!params.containsKey('name')) {
                params.name = tenant.name;
            }
            if (!params.containsKey('description')) {
                params.description = tenant.description
            }
            if (!params.containsKey('enabled')) {
                params.enabled = tenant.enabled ? 'on' : ''
            }
            if (!params.containsKey('zones')) {
                params.zones = tenant.zones?.join('\n')
            }
            def model = [tenant: tenant]
            withFormat {
                html { [parent: "/tenant/show/${params.id}", tenant: tenant, constraints: ConstraintsProcessor.getConstraints(TenantValidationCommand.class), keystoneCustomTenancy: tenantService.isKeystoneCustomTenancy()] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            flash.message = ExceptionUtils.getExceptionMessage(e)
            redirect(action: 'list')
        }
    }

    @RoleRequired('admin')
    def update = { TenantValidationCommand cmd ->
        if (cmd.hasErrors()) {
            withFormat {
                html {
                    chain(action: 'edit', model: [cmd: cmd], params: params)
                }
                xml { new XML([errors: cmd.errors]).render(response) }
                json { new JSON([errors: cmd.errors]).render(response) }
            }
        } else {
            try {
                def resp = tenantService.updateTenant(params)
                def model = [resp: resp]
                withFormat {
                    html { redirect(action: 'show', params: [id: params.id]) }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e) {
                def errors = ExceptionUtils.getExceptionMessage(e)
                withFormat {
                    html { flash.message = errors; chain(action: 'editTenant', params: params) }
                    xml { new XML([errors: errors]).render(response) }
                    json { new JSON([errors: errors]).render(response) }
                }
            }
        }
    }

    @RoleRequired('admin')
    def create = {
        withFormat {
            html { [parent: "/tenant", constraints: ConstraintsProcessor.getConstraints(TenantValidationCommand.class), keystoneCustomTenancy: tenantService.isKeystoneCustomTenancy()] }
        }

    }

    @RoleRequired('admin')
    def save = { TenantValidationCommand cmd ->
        if (cmd.hasErrors()) {
            response.status = 400
            withFormat {
                html { chain(action: 'create', model: [cmd: cmd], params: params) }
                xml { new XML([errors: cmd.errors]).render(response) }
                json { new JSON([errors: cmd.errors]).render(response) }
            }
        } else {
            try {
                def resp = tenantService.createTenant(params)
                def model = [resp: resp]
                withFormat {
                    html { redirect(action: 'list') }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e) {
                def errors = ExceptionUtils.getExceptionMessage(e)
                withFormat {
                    html { flash.message = errors; chain(action: 'create', params: params) }
                    xml { new XML([errors: errors]).render(response) }
                    json { new JSON([errors: errors]).render(response) }
                }
            }
        }
    }

    @RoleRequired('admin')
    def delete = {
        List<String> tenantIds = Requests.ensureList(params.selectedTenants ?: params.id)
        List<String> notRemovedTenantIds = []
        def deleted = []
        def error = [:]
        for (tenantId in tenantIds) {
            try {
                tenantService.deleteTenantById(tenantId)
                deleted << tenantId
            } catch (RestClientRequestException e) {
                log.error "Could not delete tenant: ${e}"
                notRemovedTenantIds << tenantId
                error[tenantId] = ExceptionUtils.getExceptionMessage(e)
            }
        }
        def flashMessage = null
        if (notRemovedTenantIds) {
            def ids = notRemovedTenantIds.join(',')
            flashMessage = "Could not delete tenants with id: ${ids}"
        }
        def model = [deleted: deleted, not_deleted_ids: notRemovedTenantIds, errors: error]
        withFormat {
            html { flash.message = flashMessage; redirect(action: 'list') }
            xml { new XML(model).render(response) }
            json { new JSON(model).render(response) }
        }
    }

    @RoleRequired('admin')
    def quotas = {
        try {
            def tenantId = params.id ?: sessionStorageService.tenant.id
            def parent = params.parent ?: "/tenant/show/${tenantId}"
            Quota[] quotas = quotaService.getQuotasByTenantId(tenantId)
            def tenantName = tenantService.getTenantById(tenantId).name
            def model = [quotas: quotas]
            withFormat {
                html { [parent: parent, quotas: quotas, tenantName: tenantName, tenantId: tenantId] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                xml { new XML([errors: error]).render(response) }
                json { new JSON([errors: error]).render(response) }
            }
        }
    }

    @RoleRequired('admin')
    def saveQuotas = {
        def quotas = params.quota
        def saved = []
        def notSaved = []
        def error = [:]
        for (quota in quotas) {
            def quotaObject = new Quota(quota)
            def displayName = quotaObject.getDisplayName()
            def name = quotaObject.getName()
            if (quotaObject.limit.matches("^\\d+\$") || (name == 'fixed_ips' && quotaObject.limit == '-1')) {
                saved << quotaObject
            } else {
                notSaved << displayName
                error[displayName] = 'Value is not an integer'
            }
        }
        def resp = ""
        def flashMessage = null
        if (notSaved) {
            def out = notSaved.join(', ')
            flashMessage = "Incorrect values for quotas: ${out}"
            response.status = 400
            def model = [resp: resp, errors: error]
            withFormat {
                html { flash.message = flashMessage; chain([action: 'quotas', params: [parent: params.parent, id: params.id]]) }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } else {
            try {
                resp = quotaService.setQuotasByTenantId(saved, params.id)
            } catch (RestClientRequestException e) {
                response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                def message = ExceptionUtils.getExceptionMessage(e)
                error[params.id] = message
            }
            def model = [resp: resp, errors: error]
            withFormat {
                html { redirect([uri: params.parent]) }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }
    }

    @RoleRequired('admin')
    def usersSave = {
        def tenantId = params.tenantId;
        Map newUsersRoles = JSON.parse(params.newUsersRoles);
        openStackUserService.changeUsersRole(newUsersRoles, tenantId)
        withFormat {
            html { render { '' } }
            xml { new XML(newUsersRoles).render(response) }
            json { new JSON(newUsersRoles).render(response) }
        }
    }

    @RoleRequired('admin')
    def users = {
        try {
            def manyUsers, tenant, roles, users, usersRoles
            manyUsers = sessionStorageService.isFlagEnabled(MANY_USERS)
            tenant = tenantService.getTenantById(params.id)
            roles = openStackUserService.allRoles
            if (!manyUsers) {
                users = openStackUserService.allUsers
                usersRoles = openStackUserService.getUsersRoles(users, params.id)
            }
            def model = [roles: roles, users: users, usersRoles: usersRoles, manyUsers: manyUsers]

            withFormat {
                html { [parent: "/tenant/show/${params.id}", tenant: tenant, roles: roles, users: users, usersRoles: usersRoles, manyUsers: manyUsers] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors: error]
            withFormat {
                html { flash.message = error; model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }
    }

}

class TenantValidationCommand {
    String name
    String description

    static constraints = {
        name(nullable: false, blank: false)
        description(nullable: false, blank: false)
    }
}
