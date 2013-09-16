package com.paypal.aurora
import groovy.json.JsonSlurper
import org.apache.commons.logging.LogFactory

/**
 * Type-checked configuration access with intelligent defaults.
 */
class ConfigService {

    private static final logger = LogFactory.getLog(this)

    static transactional = false

    def grailsApplication
    def configError

    /**
     * @return the full local system path to the directory where Aurora stores its configuration files
     */
    String getAuroraHome() {
        grailsApplication.config.auroraHome
    }


    /**
     * @return true if cloud keys and other minimum configuration has been provided, false otherwise
     */

    def reloadConfig(){
        grailsApplication.config.appConfigured = new File(auroraHome, 'Config.json').exists()
        grailsApplication.config.isValid = false
    }

    String getConfigError(){
        return configError
    }

    def readConfig(){
        grailsApplication.config.properties = new JsonSlurper().parse(new FileReader(new File(getAuroraHome(), 'Config.json')))
        grailsApplication.config.isValid = true
    }

    boolean isAppConfigured() {
        if (grailsApplication.config.appConfigured && !grailsApplication.config.isValid) {
                try{
                    readConfig()
                } catch (Exception e) {
                    if (logger.errorEnabled) {
                        logger.error(e.getMessage(), e)
                    }
                    configError = ExceptionUtils.getExceptionMessage(e)
                    grailsApplication.config.isValid = false
                }
        }
        if (!grailsApplication.config.appConfigured)
            configError = "File not found"
        grailsApplication.config.appConfigured && grailsApplication.config.isValid
    }
}
