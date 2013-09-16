package com.paypal.aurora

import com.paypal.aurora.model.FloatingIp
import com.paypal.aurora.model.IPContainer
import com.paypal.aurora.model.Instance
import org.apache.commons.codec.binary.Base64

class InstanceService {

    public static final String SEND_AVAILIBILITY_ZONE_ON_CREATE = 'instance_create_send_availibility_zone'
    public static final String SHOW_ADMIN_CREDENTIALS = 'admin_login_credentials'
    public static final String SHOW_USER_CREDENTIALS = 'user_login_credentials'

    private static final String ACTIVE_STATUS = "active"
    static transactional = false

    def openStackRESTService
    def grailsApplication
    def sessionStorageService
    def networkService
    def quantumDNSService

    def listAll(boolean fillFloatingIPs = false) {
        def resp = openStackRESTService.get(openStackRESTService.NOVA, 'servers/detail')
        List<Instance> result = [] as LinkedList
        for (server in resp.servers) {
            result << new Instance(server)
        }
        if (fillFloatingIPs) {
            if (networkService.isUseExternalFLIP()) {
                Map<String, String> flips = networkService.externalFloatingIpsMap
                result.each { instance ->
                    instance.networks.each {
                        if (flips.containsKey(it.ip)) {
                            instance.floatingIps << new IPContainer(flips.get(it.ip))
                        }
                    }
                    if (!instance.floatingIps.isEmpty()) {
                        instance.displayedIp = instance.floatingIps[0].ip
                    } else if (!instance.networks.isEmpty()) {
                        instance.displayedIp = instance.networks[0].ip
                    }
                }
            } else {
                Map<String, List<FloatingIp>> flips = networkService.floatingIps.groupBy { it.instanceId }
                result.each { instance ->
                    flips.get(instance.instanceId).each {
                        instance.floatingIps << it
                    }
                }
            }
        }
        return result
    }

    def getAllActiveInstances() {
        listAll(true).findAll() {
            it.status.toLowerCase() == ACTIVE_STATUS
        }
    }

    Instance getById(String id, boolean fillFloatingIPs = true, boolean fillFQND = false) {
        def resp = openStackRESTService.get(openStackRESTService.NOVA, "servers/$id")
        Instance instance = new Instance(resp.server)
        if (fillFQND && quantumDNSService.isEnabled()) {
            instance.networks.each {
                it.fqdn = quantumDNSService.getFqdnByIp(it.ip)
            }
        }
        if (fillFloatingIPs) {
            instance.floatingIps.addAll(networkService.getFloatingIpsForInstance(id))
            if (networkService.isUseExternalFLIP()) {
                networkService.getExternalFloatingIps().each { flip ->
                    if (instance.networks.find{it.ip == flip.fixedIpAddress})
                        instance.floatingIps << flip
                }
            }
        }
        return instance
    }

    def create(Map instance) {
        if (instance.securityGroups instanceof String)
            instance.securityGroups = [instance.securityGroups]
        def securityGroups = []
        for (groupName in instance.securityGroups) {
            securityGroups.add([name: groupName])
        }
        def body = [server: [
                name: instance.name,
                flavorRef: instance.flavor,
                imageRef: InstanceController.InstanceSources.IMAGE.toString().equals(instance.instanceSources) ? instance.image : instance.snapshot,
                min_count: 1,
                max_count: Integer.valueOf(instance.count),
                key_name: instance.keypair,
                security_groups: securityGroups
        ]]
        if (isSendAZOnCreate()) {
            body.server.availability_zone = instance.datacenter
        }
        if (instance.networks) {
            def networks = []
            Requests.ensureList(instance.networks).each { networks << [uuid: it] }
            body.networks = networks
        }
        if (instance.volumeOptions && !(InstanceController.VolumeOptions.NOT_BOOT.toString().equals(instance.volumeOptions))) {
            def blockDeviceMapping = [:]

            blockDeviceMapping.device_name = instance.deviceName
            blockDeviceMapping.volume_size = ''

            if (InstanceController.VolumeOptions.BOOT_FROM_VOLUME.toString().equals(instance.volumeOptions)) {
                blockDeviceMapping.volume_id = instance.volume
            }
            if (InstanceController.VolumeOptions.BOOT_FROM_SNAPSHOT.toString().equals(instance.volumeOptions)) {
                blockDeviceMapping.snapshot_id = instance.volumeSnapshot
            }

            blockDeviceMapping.delete_on_termination = instance.deleteOnTerminate == 'on' ? '1' : '0'

            body.server.block_device_mapping = [blockDeviceMapping]
        }

        if (instance.customizationScript) {
            body.server.user_data = Base64.encodeBase64String(instance.customizationScript.bytes)
        }

        openStackRESTService.post(openStackRESTService.NOVA, 'servers', body)
    }

    def update(Map instance) {
        openStackRESTService.put(openStackRESTService.NOVA, "servers/${instance.id}", null, [server: [name: instance.name]])
    }

    def pause(String id) {
        openStackRESTService.post(openStackRESTService.NOVA, "servers/$id/action", '{"pause": null}')
    }

    def unpause(String id) {
        openStackRESTService.post(openStackRESTService.NOVA, "servers/$id/action", '{"unpause": null}')
    }

    def suspend(String id) {
        openStackRESTService.post(openStackRESTService.NOVA, "servers/$id/action", '{"suspend": null}')
    }

    def resume(String id) {
        openStackRESTService.post(openStackRESTService.NOVA, "servers/$id/action", '{"resume": null}')
    }

    def createSnapshot(String id, String name) {
        openStackRESTService.post(openStackRESTService.NOVA, "servers/$id/action", [createImage: [name: name, metadata: [:]]])
    }

    String getLog(String id, Integer length = 35) {
        def resp = openStackRESTService.post(openStackRESTService.NOVA, "servers/$id/action", ['os-getConsoleOutput': [length: length]])
        return resp.output
    }

    String getVncUrl(String id) {
        def resp = openStackRESTService.post(openStackRESTService.NOVA, "servers/$id/action", ['os-getVNCConsole': ['type': 'novnc']])
        resp.console.url
    }

    def reboot(String id) {
        openStackRESTService.post(openStackRESTService.NOVA, "servers/$id/action", [reboot: [type: "SOFT"]])
    }

    def deleteById(String id) {
        openStackRESTService.delete(openStackRESTService.NOVA, "servers/$id")
    }

    boolean isSendAZOnCreate() {
        sessionStorageService.isFlagEnabled(SEND_AVAILIBILITY_ZONE_ON_CREATE)
    }

    boolean isShowUserLoginCredentials(){
        sessionStorageService.isFlagEnabled(SHOW_USER_CREDENTIALS) || !sessionStorageService.isFlagEnabled(SHOW_ADMIN_CREDENTIALS)
    }
    boolean isShowAdminLoginCredentials(){
        sessionStorageService.isFlagEnabled(SHOW_ADMIN_CREDENTIALS) || !sessionStorageService.isFlagEnabled(SHOW_USER_CREDENTIALS)
    }

    boolean exists(String instanceName) {
        for (Instance instance : listAll(false)) {
            if(instance.name.equals(instanceName)) {
                return true
            }
        }
        return false
    }

}


