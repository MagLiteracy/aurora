package com.paypal.asgard

class OpenStackService {

    def sessionStorageService

    def getOpenStackServices() {
        return sessionStorageService.services.values()
    }

}
