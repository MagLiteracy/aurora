package com.paypal.asgard

import grails.converters.JSON
import grails.converters.XML

class RestApiHandlerService {

    def grailsApplication

    void initialize() {
        grailsApplication.controllerClasses.each { clazz ->
            overrideRedirect(clazz)
        }
    }

    private void overrideRedirect(clazz) {
        def oldRedirect = clazz.metaClass.pickMethod("redirect", [Map] as Class[])
        clazz.metaClass.redirect = { Map args ->
            def responseBody = [:]
            def invokeRedirect = {
                oldRedirect.invoke(delegate, args)
            }
            invokeRedirect.delegate = delegate
            withFormat {
                all { invokeRedirect() }
                json { new JSON(responseBody).render(response) }
                xml { new XML(responseBody).render(response) }
            }
        }
    }

}
