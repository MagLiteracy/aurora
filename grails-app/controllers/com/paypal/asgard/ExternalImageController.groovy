package com.paypal.asgard

class ExternalImageController {

    def configService

    def index = {
        String fileName = "${configService.asgardHome}/images/${params.name}"
        File image = new File(fileName)
        response.contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName)
        response.outputStream << new FileInputStream(image)
    }
}
