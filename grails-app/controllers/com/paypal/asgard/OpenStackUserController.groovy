package com.paypal.asgard

import com.paypal.asgard.exception.RestClientRequestException
import com.paypal.asgard.util.ConstraintsProcessor
import grails.converters.JSON
import grails.converters.XML
import org.apache.shiro.grails.annotations.RoleRequired

class OpenStackUserController {

    def static allowedMethods = [list: ['GET', 'POST'], show: ['GET', 'POST'], save: ['GET', 'POST'], update: ['GET', 'POST'], delete: ['GET', 'POST']]

    static def EMAIL_REGEX = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)*\.\w+$/
    def openStackUserService
    def tenantService
    def sessionStorageService

    @RoleRequired('admin')
    def index = { redirect(action: 'list', params: params) }

    @RoleRequired('admin')
    def list = {
        def error, openStackUsers
        try{
            openStackUsers = openStackUserService.getAllUsersByTenant(sessionStorageService.tenant.id)
        } catch (RestClientRequestException e){
            openStackUsers = []
            error = ExceptionUtils.getExceptionMessage(e)
        }
        def model = [openStackUsers : openStackUsers, error : error]
        withFormat {
            html { [openStackUsers: openStackUsers, flash: [message: error]]}
            xml { new XML(model).render(response) }
            json { new JSON(model).render(response) }
        }
    }

    @RoleRequired('admin')
    def create = {
        def tenants = tenantService.getAllTenants()
        def roles = openStackUserService.getAllRoles()
        withFormat {
            html {[parent:"/openStackUser",tenants : tenants, currentTenantId: sessionStorageService.tenant.id, roles: roles,
                    constraints: ConstraintsProcessor.getConstraints(OpenStackUserCreateCommand.class)]}
            xml {new XML([tenants : tenants, roles: roles]).render(response)}
            json {new JSON([tenants : tenants, roles: roles]).render(response)}
        }
    }

    @RoleRequired('admin')
    def save = { OpenStackUserCreateCommand cmd ->
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'create', model: [cmd: cmd], params: params) }
                xml { new XML([errors:cmd.errors]).render(response) }
                json { new JSON([errors:cmd.errors]).render(response) }
            }
        } else {
            try {
                def resp = openStackUserService.createUser(params)
                def model = [resp : resp]
                withFormat {
                    html { redirect(action: 'list') }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e) {
                def errors = ExceptionUtils.getExceptionMessage(e);
                withFormat {
                    html { flash.message = errors; chain(action: 'create', params: params)}
                    xml { new XML([errors: errors]).render(response) }
                    json { new JSON([errors: errors]).render(response) }
                }
            }
        }
    }

    @RoleRequired('admin')
    def delete = {
        List<String> userIds = Requests.ensureList(params.selectedUsers ?: params.id)
        List<String> notRemovedUserIds = []
        def deleted = []
        def error = [:]
        for (userId in userIds) {
            try {
                openStackUserService.deleteUserById(userId)
                deleted << userId
            } catch (RestClientRequestException e) {
                log.error "Could not delete user: ${e}"
                notRemovedUserIds << userId
                error[userId] = ExceptionUtils.getExceptionMessage(e)
            }
        }
        def flashMessage = null
        if (notRemovedUserIds) {
            def ids = notRemovedUserIds.join(',')
            flashMessage = "Could not delete users with id: ${ids}"
        }
        def model = [deleted: deleted, not_deleted_ids : notRemovedUserIds, errors : error]
        withFormat {
            html { flash.message = flashMessage; redirect(action: 'list')}
            xml { new XML(model).render(response) }
            json { new JSON(model).render(response) }
        }
    }

    @RoleRequired('admin')
    def show = {
        def user = openStackUserService.getUserById(params.id)
        def tenant
        try {
            tenant = tenantService.getTenantById(user.tenantId)
        } catch (RestClientRequestException e) {
            tenant = [id : "", name: "No default tenant"]
        }
        withFormat {
            html {[parent:"/openStackUser",tenant : tenant, user: user]}
            xml {new XML([tenant : tenant, user: user]).render(response)}
            json {new JSON([tenant : tenant, user: user]).render(response)}
        }
    }

    @RoleRequired('admin')
    def edit = {
        def user = openStackUserService.getUserById(params.id)
        def tenants = tenantService.getAllTenants()
        boolean contain = false;
        for(def tenant : tenants) {
            contain |= tenant.id == user.tenantId;
        }
        withFormat {
            html {[parent:"/openStackUser/show/${params.id}",tenants : tenants, user: user, contain : contain,
                    constraints: ConstraintsProcessor.getConstraints(OpenStackUserUpdateCommand.class)]}
            xml {new XML([tenants : tenants, user: user, contain : contain]).render(response)}
            json {new JSON([tenants : tenants, user: user, contain : contain]).render(response)}
        }
    }

    @RoleRequired('admin')
    def update = { OpenStackUserUpdateCommand cmd ->

        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'edit', model: [cmd: cmd], params: [id : params.id]) }
                xml { new XML([errors:cmd.errors]).render(response) }
                json { new JSON([errors:cmd.errors]).render(response) }
            }

        } else {
            try {
                def resp = openStackUserService.updateUser(params)
                def model = [resp : resp]
                withFormat {
                    html { redirect(action: 'list') }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e) {
                def errors = ExceptionUtils.getExceptionMessage(e);
                withFormat {
                    html { flash.message = errors; chain(action: 'edit', params: params)}
                    xml { new XML([errors: errors]).render(response) }
                    json { new JSON([errors: errors]).render(response) }
                }
            }
        }
    }
}

class OpenStackUserCreateCommand {

    String name
    String password
    String confirm_password
    String email

    static constraints = {
        name(nullable: false, blank: false)
        password(nullable: false, blank: false, validator: {value, command ->
            if (value != command.confirm_password) {
                return "openStackUserCommand.password.confirm.error"
            }
        })
        confirm_password (nullable: false, blank: false)
        email(nullable: false, blank: false, matches: OpenStackUserController.EMAIL_REGEX)
    }
}
class OpenStackUserUpdateCommand {

    String name
    String password
    String confirm_password
    String email
    String tenant_id

    static constraints = {
        name(nullable: false, blank: false)
        password(nullable: false, blank: true, validator: {value, command ->
            if (value != command.confirm_password) {
                return "openStackUserCommand.password.confirm.error"
            }
        })
        confirm_password (nullable: false, blank: false)
        email(nullable: false, blank: false, matches: OpenStackUserController.EMAIL_REGEX)
        tenant_id(nullable: false, blank: false, notEqual: "null")
    }
}