package com.paypal.asgard.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class FloatingIp extends IPContainer {
    String instanceId
    String fixedIp
    String id

    FloatingIp() {
    }

    FloatingIp(def data) {
        this.instanceId = data.instance_id
        this.ip = data.ip
        this.fixedIp = data.fixed_ip
        this.id = data.id
        this.pool = data.pool
        canDelete = true
    }

    @Override
    public String toString() {
        return "FloatingIp{" +
                "instanceId='" + instanceId + '\'' +
                ", fixedIp='" + fixedIp + '\'' +
                ", id='" + id + '\'' +
                ", ip='" + ip + '\'' +
                ", pool='" + pool + '\'' +
                ", fqdn='" + fqdn + '\'' +
                ", canDelete=" + canDelete +
                '}';
    }
}