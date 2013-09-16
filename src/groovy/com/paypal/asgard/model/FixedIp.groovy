package com.paypal.asgard.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class FixedIp {
    String subnetId
    String ipAddress

    FixedIp() {
    }

    FixedIp(Map data) {
        subnetId = data.subnet_id
        ipAddress = data.ip_address
    }


    @Override
    public String toString() {
        return "FixedIp{" +
                "subnetId='" + subnetId + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
