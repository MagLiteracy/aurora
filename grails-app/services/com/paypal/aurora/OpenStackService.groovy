package com.paypal.aurora

class OpenStackService {

    def sessionStorageService

    def getOpenStackServices() {
        return sessionStorageService.services.values()
    }

}
