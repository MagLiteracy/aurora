package com.paypal.asgard

import com.paypal.asgard.exception.RestClientRequestException
import com.paypal.asgard.model.Stack
import grails.converters.JSON
import grails.converters.XML

import javax.servlet.http.HttpServletResponse

class HeatController {

    def static allowedMethods = [list: ['GET', 'POST'], show: ['GET', 'POST'], upload: 'POST', createStack: 'POST', delete: ['GET', 'POST']]

    def heatService
    def sessionStorageService

    def index() {redirect(action: 'list', params: params) }

    def list = {
        List <Stack> stacks = []
        try{
            stacks = heatService.listAll()
            def model = [stacks : stacks]
            withFormat {
                html { model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [stacks : stacks, errors : error, flash: [message: error]]
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                html { model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }
    }

    def delete = {
        List<String> stackIds = Requests.ensureList(params.selectedStacks ?: params.stackId)
        List<String> notRemovedStackIds = []
        def deleted = []
        def error = [:]
        for (id in stackIds) {
            try{
                heatService.delete(id)
                deleted << id
            } catch (RestClientRequestException e){
                notRemovedStackIds << id
                error[id] = ExceptionUtils.getExceptionMessage(e)
            }
        }
        def model = [deleted: deleted, not_deleted_ids : notRemovedStackIds, errors : error]
        withFormat {
            html {flash.message = error; redirect(action: 'list', model: model) }
            xml { new XML(model).render(response) }
            json { new JSON(model).render(response) }
        }
    }

    def show = {
        try {
            Stack stack = heatService.getById(params.id)
            Map model = [parent:"/heat",stack: stack]
            withFormat {
                html { model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html { flash.message = error; redirect(action: 'list') }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }
    }
    def upload = {
        try{
            def f = request.getFile('template')
            if (f.empty) {
                def error = 'file cannot be empty'
                withFormat {
                    html { flash.message = error; render(view: 'list')}
                    xml { new XML([errors: error]).render(response) }
                    json { new JSON([errors: error]).render(response) }
                }
                return
            }
            String text = f.inputStream.text
            Integer ind = sessionStorageService.putExpiringVar(text)
            def templateParams = heatService.parseParams(text)

            withFormat {
                html { render(view: 'params', model: [templateInd: ind, templateParams: templateParams]) }
                xml { new XML([templateInd: ind, templateParams: templateParams]).render(response) }
                json { new JSON([templateInd: ind, templateParams: templateParams]).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html { flash.message = error; redirect(action : 'list')}
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }

    def createStack = {
        try{
            Map<String, String> stackParams = params;
            String name = stackParams.get('stack_name')
            Integer ind = Integer.valueOf(stackParams.get('templateInd'))
            ['_action_createStack', 'action', 'controller', 'stack_name', 'templateInd'].each {stackParams.remove(it)}
            def resp = heatService.createStack(ind, name, stackParams)
            def model = [resp : resp]
            withFormat {
                html { redirect(action: 'list') }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def errors = ExceptionUtils.getExceptionMessage(e)
            withFormat {
                html { flash.message = errors; redirect(action: 'list')}
                xml { new XML([errors: errors]).render(response) }
                json { new JSON([errors: errors]).render(response) }
            }
        }
    }
}
