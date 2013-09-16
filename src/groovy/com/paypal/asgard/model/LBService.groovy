package com.paypal.asgard.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class LBService {
    String enabled
    String ip
    String name
    Integer port
    Integer weight
    String status
    Integer connectionLimit
    List<String> monitors = new ArrayList<String>()

    LBService() {
    }

    LBService(def data) {
        this.enabled = data.enabled
        this.ip = data.ip
        this.name = data.name
        this.port = data.port
        this.weight = data.weight
        this.status = data.status
        this.connectionLimit = data.connectionLimit
        this.monitors = data.monitors
    }


    @Override
    public String toString() {
        return "LBService{" +
                "enabled=" + enabled +
                ", ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                ", port=" + port +
                ", weight=" + weight +
                ", status='" + status + '\'' +
                ", connectionLimit=" + connectionLimit +
                ", monitors=" + monitors +
                '}';
    }
}
