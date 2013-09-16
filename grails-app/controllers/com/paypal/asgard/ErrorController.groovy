package com.paypal.asgard

import grails.converters.JSON
import grails.converters.XML

class ErrorController {

    def handle = {
        def exception = request.exception.cause
        if (exception instanceof groovyx.net.http.HttpResponseException && exception.statusCode == 401) {
            redirect(controller: 'auth', action: 'signOut')
        } else {
            def exceptionBody = ExceptionUtils.getExceptionBody(exception)
            withFormat {
                all { render(view: '/error') }
                json { new JSON(exceptionBody).render(response) }
                xml { new XML(exceptionBody).render(response) }
            }
        }
    }

    def formattedError = {
        def view = [:]
        view['code'] = request.getAttribute('javax.servlet.error.status_code')
        view['message'] = request.getAttribute('javax.servlet.error.message')
        view['servletName'] = request.getAttribute('javax.servlet.error.servlet_name')
        view['uri'] = request.getAttribute('javax.servlet.error.request_uri')
        withFormat {
            html {render (view: '/error')}
            json {new JSON(view).render(response)}
            xml {new XML(view).render(response)}
        }
    }
}
