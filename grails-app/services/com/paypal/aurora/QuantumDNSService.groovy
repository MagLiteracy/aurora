package com.paypal.aurora

import com.paypal.aurora.exception.QuantumDNSException

class QuantumDNSService {
    def openStackRESTService

    boolean isEnabled() {
        openStackRESTService.isServiceEnabled(OpenStackRESTService.DNS)
    }

    def addDnsRecord(String host, String ip, String zone, boolean ppp = false) {
        openStackRESTService.post(OpenStackRESTService.DNS, (ppp?'ppp.':'') + zone, getJsonBody(host, ip, zone, ppp))
    }

    def deleteDnsRecord(String host, String ip, String zone, boolean ppp = false) {
        openStackRESTService.post(OpenStackRESTService.DNS, (ppp?'ppp.':'') + zone + '/action/delete', getJsonBody(host, ip, zone, ppp))
    }

    def deleteDnsRecordByIP(String ip, String zone, boolean ppp = false) {
        String fqdn = getFqdnByIp(ip)
        if (fqdn.endsWith(zone)) {
            String host = fqdn.substring(0, fqdn.size() - zone.size() - 1)
            deleteDnsRecord(host, ip, zone)
        } else {
            String message = "Can not delete $ip from UDNS service. FQDN: $fqdn , but zone: $zone"
            log.error(message)
            throw new QuantumDNSException(message)
        }
    }

    def getJsonBody(String host, String ip, String zone, boolean ppp = false) {
        int ttl = 300
        String [] splitedIp = ip.split('\\.')
        String fqdn = host + (ppp?'.ppp.':'.') + zone
        [records:[[
                resourceType: 'A',
                records: [[
                        recordName: host,
                        timeToLive: ttl,
                        ipAddresses: [ip]
                ]]
            ], [
                resourceType: 'PTR',
                records: [[
                        recordName: splitedIp[3],
                        timeToLive: ttl,
                        fullyQualifiedName: fqdn
                ]]
            ]
        ]]
    }

    String getFqdnByIp(String ip) {
        String [] splitedIp = ip.split('\\.')
        String zone = "${splitedIp[1]}.${splitedIp[0]}.in-addr.arpa"
        String octet = splitedIp[3] + '.' + splitedIp[2]
        try {
            openStackRESTService.get(OpenStackRESTService.DNS, "$zone/recordtypes/ptr/records/$octet").fullyQualifiedName
        } catch (e) {
            log.error("Error while getting FQDN name by IP $ip", e)
            return null
        }
    }

    String getIpByHostnameAndZone(String hostname, String zone) {
        try {
            openStackRESTService.get(OpenStackRESTService.DNS, "$zone/recordtypes/a/records/$hostname").ipAddresses?.getAt(0)
        } catch (e) {
            log.error("Error while getting IP by host name $hostname and zone $zone", e)
            return null
        }
    }
}
