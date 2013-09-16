package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode


@EqualsAndHashCode
class Flavor {
    def id
    String name
    String memory
    String disk
    String ephemeral
    String swap
    String vcpu
    String rxtxFactor
    String isPublic

    Flavor() {
    }

    Flavor(Map data) {
        id = data.id
        name = data.name
        memory = data.ram
        disk = data.disk
        ephemeral = data.get('OS-FLV-EXT-DATA:ephemeral')
        swap = data.swap
        vcpu = data.vcpus
        rxtxFactor = data.rxtx_factor
        isPublic = data.get('os-flavor-access:is_public')
    }


    @Override
    public String toString() {
        return "Flavor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", memory='" + memory + '\'' +
                ", disk='" + disk + '\'' +
                ", ephemeral='" + ephemeral + '\'' +
                ", swap='" + swap + '\'' +
                ", vcpu='" + vcpu + '\'' +
                ", rxtxFactor='" + rxtxFactor + '\'' +
                ", isPublic='" + isPublic + '\'' +
                '}';
    }
}
