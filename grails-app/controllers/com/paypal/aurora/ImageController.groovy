package com.paypal.aurora

import com.paypal.aurora.exception.RestClientRequestException
import com.paypal.aurora.util.ConstraintsProcessor
import grails.converters.JSON
import grails.converters.XML
import org.apache.shiro.grails.annotations.RoleRequired

import javax.servlet.http.HttpServletResponse

class ImageController {

    def static allowedMethods = [list: ['GET','POST'], show: ['GET','POST'], save: ['GET','POST'], update: ['GET','POST'], delete: ['GET','POST']]

    def imageService

    def index = { redirect(action: 'list', params: params) }

    def list = {
        try{
            def images = imageService.getAllImages()
            def snapshots = imageService.getAllInstanceSnapshots()
            def model = [images : images, snapshots: snapshots]
            withFormat {
                html { model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [images : [], snapshots : [], errors : error, flash: [message: error]]
            withFormat {
                html { model}
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }
    }

    def show = {
        try {
            def image = imageService.getImageById(params.id)
            boolean isSnapshot = (image.type == "snapshot")
            def sType = isSnapshot?"snapshot":"image"
            def bType = isSnapshot?"Snapshot":"Image"
            def model = [image : image]
            withFormat {
                html { [parent: "/image",image: image, isSnapshot:isSnapshot, sType:sType, bType:bType] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html { flash.message = error; redirect(action: 'list')}
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }

        }
    }

    @RoleRequired('admin')
    def edit = {
        try {
            def image = imageService.getImageById(params.id)
            boolean isSnapshot = (image.type == "snapshot")
            def sType = isSnapshot?"snapshot":"image"
            def bType = isSnapshot?"Snapshot":"Image"
            if (params.containsKey('name')) {
                image.name = params.name
            }
            if (params.containsKey('shared')) {
                image.shared = params.shared == 'on'
            }
            def model = [image : image]
            withFormat {
                html { [parent: "/image/show/${params.id}",image: image, isSnapshot:isSnapshot, sType:sType, bType:bType,
                        constraints: ConstraintsProcessor.getConstraints(ImageValidationCommand.class)] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html { flash.message = error; redirect(action: 'list')}
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }
    }

    @RoleRequired('admin')
    def update = { ImageValidationCommand cmd ->
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'edit', model: [cmd: cmd], params: params) }
                xml { new XML([errors:cmd.errors]).render(response) }
                json { new JSON([errors:cmd.errors]).render(response) }
            }

        } else {
            try {
                def resp = imageService.updateImage(params)
                def model = resp
                withFormat {
                    html { redirect(action: 'show', params: [id: params.id]) }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e) {
                def error = ExceptionUtils.getExceptionMessage(e)
                def model = [errors : error]
                withFormat {
                    html { flash.message = error; chain(action: 'edit', params: params)}
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            }
        }
    }

    @RoleRequired('admin')
    def delete = {
        try {
            def resp = imageService.deleteImageById(params.id)
            def model = [resp : resp]
            withFormat {
                html { redirect(action: 'list') }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            def model = [errors : error]
            withFormat {
                html { flash.message = error; chain(action: 'show', params: params)}
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }
    }

    @RoleRequired('admin')
    def create = {
        List formats = ['ami', 'ari', 'aki', 'vhd', 'vmdk', 'raw', 'qcow2', 'vdi', 'iso']
        params.minDisk = params.minDisk?:0
        params.minRam = params.minRam?:0
        [parent: '/image/list', formats:formats, constraints: ConstraintsProcessor.getConstraints(ImageCreationCommand.class)]

    }

    @RoleRequired('admin')
    def save = { ImageCreationCommand cmd ->
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'create', model: [cmd: cmd], params: params) }
                xml { new XML([errors:cmd.errors]).render(response) }
                json { new JSON([errors:cmd.errors]).render(response) }
            }

        } else {
            try {
                def resp = imageService.createImage(params)
                def model = [resp : resp]
                withFormat {
                    html { redirect(action: 'list') }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e) {
                def error = ExceptionUtils.getExceptionMessage(e)
                def model = [errors : error]
                withFormat {
                    html { flash.message = error; chain(action: 'create', params: params)}
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            }
        }
    }

}

class ImageValidationCommand {
    String name
    String shared

    static constraints = {
        name(nullable: false, blank: false)
    }

}
class ImageCreationCommand {

    def imageService

    String name
    String location
    String diskFormat
    String shared
    Integer minDisk
    Integer minRam

    static constraints = {
        name(nullable: false, blank: false, validator: {name, command ->
            if(command.imageService.exists(name)) {
                return "imageCreationCommand.name.validator"
            }
        })
        location(nullable: false, blank: false, url: true)
        diskFormat(nullable: false,blank: false)
//        minDisk(nullable: true, min: 0 )
//        minRam(nullable: true, min: 0)
    }

}
