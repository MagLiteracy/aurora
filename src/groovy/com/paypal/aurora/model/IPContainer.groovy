package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class IPContainer {
    String ip;
    String pool;
    String fqdn;
    boolean canDelete;

    IPContainer() {
    }

    IPContainer(String ip, String pool=null) {
        this.ip = ip
        this.pool = pool
        canDelete = false
    }

    @Override
    public String toString() {
        return "IPContainer{" +
                "ip='" + ip + '\'' +
                ", pool='" + pool + '\'' +
                ", fqdn='" + fqdn + '\'' +
                ", canDelete=" + canDelete +
                '}';
    }
}
