package com.paypal.aurora

class ExternalImageController {

    def configService

    def index = {
        String fileName = "${configService.auroraHome}/images/${params.name}"
        File image = new File(fileName)
        response.contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName)
        response.outputStream << new FileInputStream(image)
    }
}
