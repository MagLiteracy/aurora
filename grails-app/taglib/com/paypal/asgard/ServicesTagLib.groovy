package com.paypal.asgard

class ServicesTagLib {

    def openStackRESTService

    def ifServiceEnabled = { attrs, body ->
        if (openStackRESTService.isServiceEnabled(attrs['name'])) {
            out << body()
        }
    }
}
