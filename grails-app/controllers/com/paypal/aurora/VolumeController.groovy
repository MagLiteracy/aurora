package com.paypal.aurora

import com.paypal.aurora.exception.RestClientRequestException
import com.paypal.aurora.util.ConstraintsProcessor
import grails.converters.JSON
import grails.converters.XML
import org.apache.shiro.grails.annotations.RoleRequired

class VolumeController {

    def static allowedMethods = [list: ['GET','POST'], save: 'POST', show: ['GET','POST'],  delete: ['GET','POST'], attach: ['GET','POST'], detach: ['GET','POST'],saveType: ['GET','POST'],showType: ['GET','POST'],deleteType: ['GET','POST']]

    def volumeService
    def instanceService

    def index = { redirect(action: 'list', params: params) }

    def list = {
        def volumes = []
        def volumeTypes = []
        def error
        try{
            volumes = volumeService.getAllVolumes()
            volumeTypes = volumeService.getAllVolumeTypes()
        } catch (RestClientRequestException e){
            error = ExceptionUtils.getExceptionMessage(e)
        }
        def model = [volumes: volumes, volumeTypes: volumeTypes, errors : error, flash: [message: error]]
        withFormat {
            html { model}
            xml { new XML(model).render(response) }
            json { new JSON(model).render(response) }
        }
    }

    def _volumes = {
        def volumes = []
        def error
        try{
            volumes = volumeService.getAllVolumes()
        } catch (RestClientRequestException e){
            error = ExceptionUtils.getExceptionMessage(e)
        }
        def model = [volumes: volumes, errors : error]
        withFormat {
            html { flash.message = error; render(view: '_volumes', model: model)}
            xml { new XML(model).render(response) }
            json { new JSON(model).render(response) }
        }
    }

    def show = {
        try {
            def volume = volumeService.getVolumeById(params.id)
            def model = [volume : volume]
            withFormat {
                html { [parent:"/volume", volume: volume] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def errors = ExceptionUtils.getExceptionMessage(e)
            withFormat {
                html { flash.message = errors; redirect(action: 'list')}
                xml { new XML([errors: errors]).render(response) }
                json { new JSON([errors: errors]).render(response) }
            }
        }
    }

    def create = {
        def volumeTypes = volumeService.getAllVolumeTypes()
        def availablePlaceInQuotas = volumeService.getAvailablePlaceInQuotas()
        Integer gigabytesAvailable = availablePlaceInQuotas[QuotaService.GIGABYTES]
        Integer volumeAvailable = availablePlaceInQuotas[QuotaService.VOLUMES]
        withFormat {
            html { [parent:"/volume", GBAvailable : gigabytesAvailable, VAvailable : volumeAvailable, volumeTypes: volumeTypes
                    , constraints: ConstraintsProcessor.getConstraints(VolumeCreateCommand.class)] }
            xml { new XML([GBAvailable : gigabytesAvailable, VAvailable : volumeAvailable, volumeTypes: volumeTypes]).render(response) }
            json { new JSON([GBAvailable : gigabytesAvailable, VAvailable : volumeAvailable, volumeTypes: volumeTypes]).render(response) }
        }
    }


    def save = {
        VolumeCreateCommand cmd ->
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'create', model: [cmd : cmd], params: params) }
                xml { new XML([errors:cmd.errors]).render(response) }
                json { new JSON([errors:cmd.errors]).render(response) }
            }
        } else {
            try {
                def resp = volumeService.createVolume(params)
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

    def edit = {
        def volume = volumeService.getVolumeById(params.id)
        withFormat {
            html { [parent: "volume/show/$params.id", volume: volume, params : [name : volume.displayName, description : volume.description]] }
            xml { new XML([volume: volume, params : [name : volume.displayName, description : volume.description]]).render(response) }
            json { new JSON([volume: volume, params : [name : volume.displayName, description : volume.description]]).render(response) }
        }
    }

    def update = {
        try {
            def resp = volumeService.updateVolume(params)
            def model = [resp : resp]
            withFormat {
                html { redirect(action: 'list') }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def errors = ExceptionUtils.getExceptionMessage(e)
            withFormat {
                html { flash.message = errors; chain(action: 'edit', params: params)}
                xml { new XML([errors: errors]).render(response) }
                json { new JSON([errors: errors]).render(response) }
            }
        }
    }

    def delete = {
        List<String> volumeIds = Requests.ensureList(params.selectedVolumes ?: params.id)
        List<String> notRemovedVolumeIds = []
        def deleted = []
        def error = [:]
        for (volumeId in volumeIds) {
            try {
                volumeService.deleteVolumeById(volumeId)
                deleted << volumeId
            } catch (RestClientRequestException e) {
                log.error "Could not delete volume: ${e}"
                notRemovedVolumeIds << volumeId
                error[volumeId] = ExceptionUtils.getExceptionMessage(e)
            }
        }
        def flashMessage = null
        if (notRemovedVolumeIds) {
            def ids = notRemovedVolumeIds.join(',')
            flashMessage = "Could not delete volumes with id: ${ids}"
        }
        def model = [deleted: deleted, not_deleted_ids : notRemovedVolumeIds, errors : error]
        withFormat {
            html { flash.message = flashMessage; redirect(action: 'list')}
            xml { new XML(model).render(response) }
            json { new JSON(model).render(response) }
        }
    }

    def editAttach = {
        def volume = volumeService.getVolumeById(params.id);
        def instances = instanceService.getAllActiveInstances()
        withFormat {
            html { [parent:"/volume/show/$params.id", instances: instances, volume: volume] }
            xml { new XML([instances: instances, volume: volume]).render(response) }
            json { new JSON([instances: instances, volume: volume]).render(response) }
        }
    }

    def attach = {
        try {
            def resp = volumeService.attachToInstance(params)
            def model = [resp : resp]
            withFormat {
                html { redirect(action: 'list') }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def errors = ExceptionUtils.getExceptionMessage(e)
            withFormat {
                html { redirect(action: 'list') }
                xml { flash.message = errors; new XML([errors: errors]).render(response)}
                json { new JSON([errors: errors]).render(response) }
            }
        }
    }

    def detach = {
        try {
            def resp = volumeService.detachFromInstance(params)
            def model = [resp : resp]
            withFormat {
                html { redirect(action: 'list') }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def errors = ExceptionUtils.getExceptionMessage(e)
            withFormat {
                html { flash.message = errors; redirect(action: 'list')}
                xml { new XML([errors: errors]).render(response) }
                json { new JSON([errors: errors]).render(response) }
            }
        }

    }

    @RoleRequired('admin')
    def createType = {
        withFormat {
            html { [parent: "/volume", constraints: ConstraintsProcessor.getConstraints(VolumeTypeCreateCommand.class)] }
        }
    }

    @RoleRequired('admin')
    def saveType = { VolumeTypeCreateCommand cmd ->
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'createType', model: [cmd : cmd], params: params) }
                xml { new XML([error : cmd.errors]).render(response)}
                json { new JSON([error : cmd.errors]).render(response)}
            }
        } else {
            try {
                def resp = volumeService.createVolumeType(params.name)
                def model = [resp : resp]
                withFormat {
                    html { redirect(action: 'list') }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e) {
                def errors = ExceptionUtils.getExceptionMessage(e)
                withFormat {
                    html { flash.message = errors; chain(action: 'createType', params: params)}
                    xml { new XML([errors: errors]).render(response) }
                    json { new JSON([errors: errors]).render(response) }
                }
            }
        }
    }

    @RoleRequired('admin')
    def showType = {
        def volumeType = volumeService.getVolumeTypeById(params.id)
        withFormat {
            html { [parent: "/volume", volumeType: volumeType] }
            xml { new XML([volumeType: volumeType]).render(response) }
            json { new JSON([volumeType: volumeType]).render(response) }
        }

    }

    @RoleRequired('admin')
    def deleteType = {
        List<String> volumeTypeIds = Requests.ensureList(params.selectedVolumeTypes)
        List<String> notRemovedVolumeTypeIds = []
        def deleted = []
        def error = [:]
        for (volumeTypeId in volumeTypeIds) {
            try {
                volumeService.deleteVolumeTypeById(volumeTypeId)
                deleted << volumeTypeId
            } catch (RestClientRequestException e) {
                log.error "Could not delete volume type: ${e}"
                notRemovedVolumeTypeIds << volumeTypeId
                error[volumeTypeId] = ExceptionUtils.getExceptionMessage(e)
            }
        }
        def flashMessage = null
        if (notRemovedVolumeTypeIds) {
            def ids = notRemovedVolumeTypeIds.join(',')
            flashMessage = "Could not delete volume types with id: ${ids}"
        }
        def view = [deleted: deleted, not_deleted_ids : notRemovedVolumeTypeIds, errors : error]
        withFormat {
            html { flash.message = flashMessage; redirect(action: 'list')}
            xml { new XML(view).render(response) }
            json { new JSON(view).render(response) }
        }
    }
}

class VolumeCreateCommand {

    def volumeService

    String name
    String size

    static constraints = {
        name(nullable: false, blank: false, validator: {name, command ->
            if(command.volumeService.exists(name)) {
                return "volumeCreateCommand.name.validator"
            }
        })
        size(nullable: false, blank: false, validator: {size, obj ->
            return ValidatorUtils.checkInteger(size)
        })
    }
}

class VolumeTypeCreateCommand {

    String name

    static constraints = {
        name(nullable: false, blank: false)
    }
}
