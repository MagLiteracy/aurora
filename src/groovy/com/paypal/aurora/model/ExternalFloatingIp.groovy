package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class ExternalFloatingIp extends IPContainer {
    String routerId;
    String tenantId;
    String floatingNetworkId;
    String fixedIpAddress;
    String floatingIpAddress;
    String portId;
    String id;

    ExternalFloatingIp() {
    }

    ExternalFloatingIp(def data) {
        routerId = data.router_id
        tenantId = data.tenant_id
        floatingNetworkId = data.floating_network_id
        fixedIpAddress = data.fixed_ip_address
        floatingIpAddress = data.floating_ip_address
        portId = data.port_id
        id = data.id
        ip = floatingIpAddress
        canDelete = false
    }


    @Override
    public String toString() {
        return "ExternalFloatingIp{" +
                "routerId='" + routerId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", floatingNetworkId='" + floatingNetworkId + '\'' +
                ", fixedIpAddress='" + fixedIpAddress + '\'' +
                ", floatingIpAddress='" + floatingIpAddress + '\'' +
                ", portId='" + portId + '\'' +
                ", id='" + id + '\'' +
                ", ip='" + ip + '\'' +
                ", pool='" + pool + '\'' +
                ", fqdn='" + fqdn + '\'' +
                ", canDelete=" + canDelete +
                '}';
    }
}
