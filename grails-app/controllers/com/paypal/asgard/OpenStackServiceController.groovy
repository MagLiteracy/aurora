package com.paypal.asgard

import com.paypal.asgard.exception.RestClientRequestException
import grails.converters.JSON
import grails.converters.XML

class OpenStackServiceController {

    def static allowedMethods = [list: ['GET', 'POST']]

    def openStackService

    def index = { redirect(action: 'list', params: params) }

    def list = {
        try {
            def openStackServices = openStackService.getOpenStackServices()
            def model = [openStackServices: openStackServices]
            withFormat {
                html { model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [openStackServices: [], errors: error, flash: [message: error]]
            withFormat {
                html { model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }
    }
}
