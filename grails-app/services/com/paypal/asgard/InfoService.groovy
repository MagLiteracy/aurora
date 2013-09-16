package com.paypal.asgard

class InfoService {

    def buildDate, buildNumber

    def getInfo(){
        def info = ['Build Number' : getBuildNumber(), 'Build Date' : getBuildDate()]
        return info
    }

}
