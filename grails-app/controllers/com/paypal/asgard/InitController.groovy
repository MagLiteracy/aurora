package com.paypal.asgard

class InitController {

    def configService


    def beforeInterceptor = {
        configService.reloadConfig()
        if (configService.appConfigured) {
            redirect(controller: 'auth')
            return false
        }
    }

    def index = {
        [asgardHome: configService.asgardHome, errorMessage : configService.configError]
    }
}
