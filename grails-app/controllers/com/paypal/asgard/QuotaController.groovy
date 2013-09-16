package com.paypal.asgard

import com.paypal.asgard.exception.RestClientRequestException
import grails.converters.JSON
import grails.converters.XML

import javax.servlet.http.HttpServletResponse

class QuotaController {

    def static allowedMethods = [list: ['GET', 'POST']]

    def quotaService
    def sessionStorageService

    def index = { redirect(action: 'list', params: params) }

    def list = {
        try{
            def quotas = quotaService.getAllQuotas()
            def model = [quotas : quotas]
            withFormat {
                html { [quotas : quotas] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            def quotas = []
            def model = [quotas : quotas, errors : error, flash: [message: error]]
            withFormat {
                html { model}
                xml { new XML(model).render(response)}
                json { new JSON(model).render(response)}
            }
        }
    }
}
