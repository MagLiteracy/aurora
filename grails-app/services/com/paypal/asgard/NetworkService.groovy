package com.paypal.asgard

import com.paypal.asgard.model.ExternalFloatingIp
import com.paypal.asgard.model.FloatingIp

class NetworkService {

    private static final String FLOATING_IPS = 'os-floating-ips'
    private static final String PREFIX = 'v2.0'
    private static final String POOLS = "os-floating-ip-pools"
    private static final String EXTERNAL_FLOATING_IPS = 'external_floating_ips'
    private static final String QUANTUM_FLIP_PATH = 'floatingips'

    def openStackRESTService

    def sessionStorageService

    def quantumDNSService

    def getFloatingIps() {
        List<FloatingIp> floatingIps = []
        def resp = openStackRESTService.get(openStackRESTService.NOVA, FLOATING_IPS)
        resp.floating_ips.each{
            FloatingIp ip = new FloatingIp(it)
            if (quantumDNSService.isEnabled()) {
                ip.fqdn = quantumDNSService.getFqdnByIp(ip.ip)
            }
            floatingIps << ip
        }
        return floatingIps
    }
    def getUnassignedFloatingIps() {
        def allIps = getFloatingIps()
        def unassigned = []
        allIps.each {
            if (!it.instanceId){
                unassigned << it
            }
        }
        unassigned
    }

    List<ExternalFloatingIp> getExternalFloatingIps() {
        List<ExternalFloatingIp> floatingIps = []
        def resp = openStackRESTService.get(openStackRESTService.QUANTUM, "$PREFIX/$QUANTUM_FLIP_PATH")
        resp.floatingips.each {
            floatingIps << new ExternalFloatingIp(it)
        }
        return floatingIps
    }

    Map<String, String> getExternalFloatingIpsMap() {
        Map<String, String> map = [:]
        getExternalFloatingIps().each {
            map.put(it.fixedIpAddress, it.floatingIpAddress)
        }
        return map
    }

    def getFloatingIpsForInstance(def instanceId){
        def allIps = getFloatingIps()
        def matched = []
        allIps.each {
            if (it.instanceId == instanceId){
                matched << it
            }
        }
        matched
    }

    def getFloatingIpById(def id){
        def resp = openStackRESTService.get(openStackRESTService.NOVA, FLOATING_IPS+"/$id")
        new FloatingIp(resp.floating_ip)
    }

    def getFloatingIpPools(){
        def get = openStackRESTService.get(openStackRESTService.NOVA, POOLS)
        get.floating_ip_pools
    }

    def allocateFloatingIp(def pool, String hostname=null, String zone=null){
        def resp = openStackRESTService.post(openStackRESTService.NOVA, FLOATING_IPS, [pool: pool])
        if (quantumDNSService.isEnabled()) {
            quantumDNSService.addDnsRecord(hostname, resp.floating_ip.ip, zone)
        }
        return resp
    }

    def releaseFloatingIp(def ip){
        openStackRESTService.delete(openStackRESTService.NOVA, FLOATING_IPS+"/${ip}")
        if (quantumDNSService.isEnabled()) {
            quantumDNSService.deleteDnsRecordByIP(ip, sessionStorageService.tenant.zone)
        }
    }

    def associateFloatingIp(def instanceId, def ip){
        def body = [addFloatingIp:[address:ip]]
        openStackRESTService.post(openStackRESTService.NOVA, "servers/${instanceId}/action", body)
    }

    def disassociateFloatingIp(def instanceId, def ip){
        def body = [removeFloatingIp:[address:ip]]
        openStackRESTService.post(openStackRESTService.NOVA, "servers/${instanceId}/action", body)
    }

    boolean isUseExternalFLIP() {
        sessionStorageService.isFlagEnabled(EXTERNAL_FLOATING_IPS)
    }

}
