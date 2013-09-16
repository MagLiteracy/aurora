package com.paypal.asgard

import com.paypal.asgard.exception.RestClientRequestException
import com.paypal.asgard.exception.UserStateException
import com.paypal.asgard.model.UserState
import grails.converters.JSON
import grails.converters.XML

import javax.servlet.http.HttpServletResponse

class UserStateController {

    def static allowedMethods = [changeTenant: ['GET','POST'], changeDataCenter: ['GET','POST'], getAllDataCenters: 'GET',getCurrentUserState: 'GET']

    def openStackRESTService

    def sessionStorageService

    def changeDataCenter = {
        def userState, model
        def currentDatacenter = sessionStorageService.dataCenterName
        def currentId = sessionStorageService.tenant.id
        try{
            def dataCenterName = checkParamValue('dataCenterName')
            userState = openStackRESTService.changeUserState(dataCenterName, sessionStorageService.tenant.id)
            model = [userState : userState]
        } catch (RestClientRequestException e) {
            log.info(e)
            userState = new UserState(currentDatacenter, currentId)
            def error = ExceptionUtils.getExceptionMessage(e)
            flash.message = error
            model = [userState : userState, errors : error]
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        }
        defineResponse(model)
    }

    def getAllDataCenters = {
        try{
            def keySet = sessionStorageService.dataCentersMap.keySet()
            def model = [keySet : keySet]
            withFormat {
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e){
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            def error = ExceptionUtils.getExceptionMessage(e)
            withFormat {
                xml { new XML([errors : error]).render(response)}
                json { new JSON([errors : error]).render(response)}
            }
        }
    }

    def getCurrentUserState = {
        def userState
        def error
        try{
            userState = new UserState (sessionStorageService.dataCenterName,sessionStorageService.tenant.id)
        }catch (RestClientRequestException e){
            error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        }
        def model = [userState : userState, errors : error]
        defineResponse(model)
    }

    def changeTenant = {
        def model
        try{
            def tenantId = checkParamValue('tenantId')
            def userState = openStackRESTService.changeUserState(sessionStorageService.dataCenterName, tenantId)
            model = [userState : userState]
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            flash.message = error
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            model = [errors : error]
        }
        defineResponse(model)
    }

    def private checkParamValue(def param) {
        def value = params.get(param)
        if (!value) {
            throw new UserStateException("Parameter '${param}' can't be null")
        }
        value
    }

    def private defineResponse(def view) {
        withFormat {
            html { redirect(uri: params.targetUri?:'/', model: view) }
            xml { new XML(view).render(response) }
            json { new JSON(view).render(response) }
        }
    }

}
