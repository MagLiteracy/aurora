package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class ExternalGatewayInfo{
    String networkId
    String networkName
    Boolean enableSnat

    ExternalGatewayInfo(Map data) {
        networkId = data.network_id
        enableSnat = data.enable_snat
    }

    ExternalGatewayInfo() {
    }


    @Override
    public String toString() {
        return "ExternalGatewayInfo{" +
                "networkId='" + networkId + '\'' +
                ", enableSnat=" + enableSnat +
                '}';
    }
}
