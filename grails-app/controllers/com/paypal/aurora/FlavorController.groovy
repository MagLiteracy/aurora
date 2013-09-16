package com.paypal.aurora

import com.paypal.aurora.exception.RestClientRequestException
import com.paypal.aurora.model.Flavor
import com.paypal.aurora.util.ConstraintsProcessor
import grails.converters.JSON
import grails.converters.XML
import org.apache.shiro.grails.annotations.RoleRequired

import javax.servlet.http.HttpServletResponse

class FlavorController {

    def flavorService
    def static allowedMethods = [list: ['GET','POST'],save: ['GET','POST'], delete: ['POST', 'GET']]

    def index = { redirect(action: 'list', params: params) }

    def list = {
        List<Flavor> flavors = []
        try{
            flavors = flavorService.listAll()
            def model = [flavors : flavors]
            withFormat {
                html { model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [flavors: flavors, errors : error, flash: [message: error]]
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                html { model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }
    }

    @RoleRequired('admin')
    def delete = {
        List<String> flavorIds = Requests.ensureList(params.selectedFlavors ?: params.flavorId)
        List<String> notRemovedFlavorIds = []
        def deleted = []
        def error = [:]
        for (id in flavorIds) {
            try{
                flavorService.delete(id)
                deleted << id
            } catch (RestClientRequestException e){
                notRemovedFlavorIds << id
                error[id] = ExceptionUtils.getExceptionMessage(e)
            }
        }
        def view = [deleted : deleted, not_deleted_ids : notRemovedFlavorIds, error : error]
        withFormat {
            html { redirect(action: 'list') }
            xml { new XML(view).render(response) }
            json { new JSON(view).render(response) }
        }
    }

    @RoleRequired('admin')
    def create = {
        params.with {
            if (!fromUser) {
                ram = 512
                disk = 20
                vcpus = 2
                isPublic = 'on'
            }
        }
        [parent:"/flavor", constraints: ConstraintsProcessor.getConstraints(FlavorCreateCommand.class)]
    }

    @RoleRequired('admin')
    def save = { FlavorCreateCommand cmd ->
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'create', model: [cmd: cmd], params: params) }
                xml { new XML([errors:cmd.errors]).render(response) }
                json { new JSON([errors:cmd.errors]).render(response) }
            }
        } else {
            try {
                def resp = flavorService.create(params)
                def model = [resp : resp]
                withFormat {
                    html { redirect(action: 'list') }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e) {
                def errors = ExceptionUtils.getExceptionMessage(e)
                withFormat {
                    html { flash.message = errors; chain(action: 'create', params: params)}
                    xml { new XML([errors: errors]).render(response) }
                    json { new JSON([errors: errors]).render(response) }
                }
            }
        }
    }

}

class FlavorCreateCommand {

    def quotaService
    def sessionStorageService

    String name
    String ram
    String disk
    String vcpus
    String isPublic
    String ephemeral
    String swap
    String rxtxFactor

    static constraints = {
        name(nullable: false, blank: false)
        ram(nullable: false, blank: false, validator: { ram, obj ->
            return ValidatorUtils.checkInteger(ram)
        })
        disk(nullable: false, blank: false, validator: {disk, obj ->
            return ValidatorUtils.checkInteger(disk)
        })
        vcpus(nullable: false, blank: false, validator: {vcpus, obj ->
            return ValidatorUtils.checkInteger(vcpus)
        })
        ephemeral(nullable: true, validator: {ephemeral, obj ->
            return ValidatorUtils.checkInteger(ephemeral)
        })
        swap(nullable: true, validator: {swap, obj ->
            return ValidatorUtils.checkInteger(swap)
        })
        rxtxFactor(nullable: true, validator: {rxtxFactor, obj ->
            return ValidatorUtils.checkDouble(rxtxFactor)
        })
    }
}
