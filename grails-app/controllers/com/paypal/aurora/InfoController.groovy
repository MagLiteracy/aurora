package com.paypal.aurora

import com.paypal.aurora.exception.RestClientRequestException
import grails.converters.JSON
import grails.converters.XML

import javax.servlet.http.HttpServletResponse

class InfoController {

    final static allowedMethods = [index : ['GET']]

    def date, number
    def infoService

    def index = {
        try{
            def info = infoService.getInfo()
            def model = [info : info]
            withFormat {
                html {model}
                xml {new XML(model).render(response)}
                json{new JSON(model).render(response)}
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            def model = [error : error, flash: [message: error]]
            withFormat {
                html { model}
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }
}
