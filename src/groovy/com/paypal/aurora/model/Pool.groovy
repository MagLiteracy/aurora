package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Pool {
    String enabled
    String method
    String name
    String status
    List<String> monitors = new ArrayList<String>()
    List<LBService> services = new ArrayList<LBService>()

    Pool() {
    }

    Pool(def data) {
        this.enabled = data.enabled
        this.method = data.method
        this.name = data.name
        this.status = data.status
        this.monitors = data.monitors
        this.services = data.services
    }

    @Override
    public String toString() {
        return "Pool{" +
                "enabled='" + enabled + '\'' +
                ", method='" + method + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", monitors=" + monitors +
                ", services=" + services +
                '}';
    }
}
