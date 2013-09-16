package com.paypal.aurora

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
        [auroraHome: configService.auroraHome, errorMessage : configService.configError]
    }
}
