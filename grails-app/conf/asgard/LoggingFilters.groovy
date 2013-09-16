package asgard

import com.paypal.asgard.Constant
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class LoggingFilters {

    def sessionStorageService

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                if (log.isInfoEnabled()) {
                    String message = "Request from user: ${request.userPrincipal?.name} " +
                            "datacenter: $sessionStorageService.dataCenterName " +
                            "tenant: ${sessionStorageService.tenant?.name}\n" +
                            "to URI: $request.requestURI\n" +
                            "Controller: $controllerName, Action: $actionName"
                    if (log.isDebugEnabled()) {
                        GrailsParameterMap paramsForLog = params.clone()
                        Constant.NOT_LOGGING_PARAMETERS.each {paramsForLog.remove(it)}
                        message += "\n" +
                                "Parameters: $paramsForLog"
                    }
                    log.info message
                }
            }
            after = { Map model ->
                if (log.isDebugEnabled()) {
                    if (request.isRequestedSessionIdValid()){
                        log.debug "Response to user: ${request.userPrincipal?.name} " +
                                "datacenter: $sessionStorageService.dataCenterName " +
                                "tenant: ${sessionStorageService.tenant?.name}\n" +
                                "from URI: $request.requestURI\n" +
                                "Controller: $controllerName, Action: $actionName\n" +
                                "Model: $model"
                    }
                }
            }
            afterView = { Exception e ->
                if (e) {
                    log.error "Error in view user: ${request.userPrincipal?.name} " +
                            "datacenter: $sessionStorageService.dataCenterName " +
                            "tenant: ${sessionStorageService.tenant?.name}\n" +
                            "from URI: $request.requestURI\n" +
                            "Controller: $controllerName, Action: $actionName\n" +
                            "Exception message: $e.message"
                }
            }
        }
    }
}
