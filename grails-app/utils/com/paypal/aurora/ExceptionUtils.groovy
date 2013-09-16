package com.paypal.aurora

import com.paypal.aurora.exception.RestClientRequestException
import org.apache.commons.logging.LogFactory

class ExceptionUtils {

    private static final logger = LogFactory.getLog(this)

    def static getExceptionMessage(Exception exception) {
        if (exception instanceof RestClientRequestException) {
            getMessage(exception.getCause())
        } else {
            getMessage(exception)
        }
    }

    def private static getMessage(Throwable throwable) {
        try {
            if (throwable.respondsTo('getResponse')) {
                def throwableResponse = throwable.getResponse()
                if (throwableResponse) {
                    if (throwableResponse.respondsTo('getData')) {
                        def exceptionResponseData = throwableResponse.getData()
                        if (exceptionResponseData) {
                            if (exceptionResponseData instanceof Map) {
                                if (exceptionResponseData.QuantumError) {
                                    return exceptionResponseData.QuantumError
                                }
                                def value = exceptionResponseData.find { it }.value
                                if (value instanceof Map) {
                                    return value.message?:throwable.getMessage()
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (logger.errorEnabled) {
                logger.error(ex.getMessage(), ex)
            }
        }
        throwable.getMessage()
    }

    def static getExceptionBody(Exception exception) {
        def body = [error: getExceptionMessage(exception)]
        body.cause = org.apache.commons.lang.exception.ExceptionUtils.getRootCauseMessage(exception)
        body.stacktrace = org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace(exception)
        body
    }

}
