package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Port {

    Boolean adminStateUp
    String deviceId
    String deviceOwner
    String id
    String macAddress
    String name
    String networkId
    List<String> securityGroupsId
    List<SecurityGroup> securityGroups = []
    List<FixedIp> fixedIps
    String status
    String tenantId

    Port() {

    }

    Port(Map data) {
        adminStateUp = data.admin_state_up
        deviceId = data.device_id
        deviceOwner = data.device_owner
        id = data.id
        macAddress = data.mac_address
        name = data.name
        fixedIps = data.fixed_ips
        networkId = data.network_id
        securityGroupsId = data.security_groups
        status = data.status
        tenantId = data.tenant_id
    }


    @Override
    public String toString() {
        return "Port{" +
                "adminStateUp=" + adminStateUp +
                ", deviceId='" + deviceId + '\'' +
                ", deviceOwner='" + deviceOwner + '\'' +
                ", id='" + id + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", name='" + name + '\'' +
                ", networkId='" + networkId + '\'' +
                ", securityGroupsId=" + securityGroupsId +
                ", securityGroups=" + securityGroups +
                ", status='" + status + '\'' +
                ", tenantId='" + tenantId + '\'' +
                '}';
    }
}
