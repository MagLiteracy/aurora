package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Vip {
    String name
    String ip
    String port
    String protocol
    String enabled

    Vip() {

    }

    Vip(Map data) {
        name = data.name
        ip = data.ip
        port = data.port
        protocol = data.protocol
        enabled = data.enabled
    }



    @Override
    public String toString() {
        return "Vip{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", protocol='" + protocol + '\'' +
                ", enabled='" + enabled + '\'' +
                '}';
    }
}
