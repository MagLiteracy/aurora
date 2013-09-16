package com.paypal.asgard

import com.paypal.asgard.model.Volume

class VolumeService {

    private static final String VOLUMES = 'volumes'
    private static final String TYPES = 'types'
    private static final String VOLUME_ATTACHMENT = "os-volume_attachments"
    private static final String SERVERS = "servers"
    static transactional = false

    def openStackRESTService
    def sessionStorageService
    def instanceService
    def quotaService

    def getAllVolumes(boolean fillInstanceName = true) {
        def resp = openStackRESTService.get(openStackRESTService.NOVA_VOLUME, "${VOLUMES}/detail")
        def volumes = []
        for (volume in resp.volumes) {
            def volumeAdd = new Volume(volume)
            if (volumeAdd.instanceId && fillInstanceName) {
                try {
                    volumeAdd.instanceName = instanceService.getById(volumeAdd.instanceId, false).name
                } catch (Exception e) {
                    log.warn("Failed to retriev instance [${volumeAdd.instanceId}] for volume [${volumeAdd.id}].")
                }
            }
            volumes << volumeAdd

        }
        volumes
    }

    def getVolumeById(def volumeId) {
        def resp = openStackRESTService.get(openStackRESTService.NOVA_VOLUME, "$VOLUMES/$volumeId")
        def volume = new Volume(resp.volume)
        if (volume.instanceId) {
            volume.instanceName = instanceService.getById(volume.instanceId, false).name
        }
        return volume
    }

    def deleteVolumeById(def volumeId) {
        openStackRESTService.delete(openStackRESTService.NOVA_VOLUME, "$VOLUMES/$volumeId")
    }

    def createVolume(def volume) {
        def body = [volume: [status: "creating", availability_zone: null, source_volid: null,
                display_description: volume.description, snapshot_id: null, user_id: null, size: volume.size, display_name: volume.name,
                imageRef: null, attach_status: "detached", volume_type: volume.type, project_id: null, metadata: []]]
        openStackRESTService.post(openStackRESTService.NOVA_VOLUME, VOLUMES, body)
    }

    def updateVolume(def volume) {
        def body = [volume: [display_name: volume.name, display_description: volume.description]]
        openStackRESTService.put(openStackRESTService.NOVA_VOLUME, "$VOLUMES/$volume.id", body)
    }

    def attachToInstance(def volume) {
        def body = [volumeAttachment: [device: volume.device, volumeId: volume.id]]
        openStackRESTService.post(openStackRESTService.NOVA, "$SERVERS/$volume.instanceId/$VOLUME_ATTACHMENT", body);
    }
    def detachFromInstance(def volume) {
        openStackRESTService.delete(openStackRESTService.NOVA, "$SERVERS/$volume.instanceId/$VOLUME_ATTACHMENT/$volume.id")
    }

    def getAllVolumeTypes() {
        openStackRESTService.get(openStackRESTService.NOVA_VOLUME, TYPES).volume_types
    }

    def createVolumeType(def name) {
        def body = [volume_type: [name: name]]
        openStackRESTService.post(openStackRESTService.NOVA_VOLUME, TYPES, body)
    }

    def getVolumeTypeById(def typeId) {
        openStackRESTService.get(openStackRESTService.NOVA_VOLUME, "$TYPES/$typeId").volume_type
    }

    def deleteVolumeTypeById(def typeId) {
        openStackRESTService.delete(openStackRESTService.NOVA_VOLUME, "$TYPES/$typeId")
    }

    boolean exists(String volumeName) {
        for (Volume volume : getAllVolumes(false)) {
            if(volume.displayName.equals(volumeName)) {
                return true
            }
        }
        return false
    }

    def getAvailablePlaceInQuotas() {
        def volumes = getAllVolumes(false)
        def gigabyteQuota = quotaService.getQuotaByName(QuotaService.GIGABYTES)
        def volumeQuota = quotaService.getQuotaByName(QuotaService.VOLUMES)
        def gigabytesAvailable = Integer.parseInt(gigabyteQuota.limit)
        for (Volume volume : volumes) {
            gigabytesAvailable -= Integer.parseInt(volume.size)
        }
        def availablePlaceInQuotas = [:]
        availablePlaceInQuotas[QuotaService.GIGABYTES] = Math.max(gigabytesAvailable, 0);
        availablePlaceInQuotas[QuotaService.VOLUMES] = Math.max(Integer.parseInt(volumeQuota.limit) - volumes.size(), 0)
        return availablePlaceInQuotas
    }
}
