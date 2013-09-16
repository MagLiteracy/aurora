package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Tenant implements Serializable {

    private static final long serialVersionUID = - 2922296541913072304L

    String id
    String name
    String description
    String enabled
    String [] zones

    Tenant() {

    }

    Tenant(Map data) {
        id = data.id
        name = data.name
        description = data.description
        enabled = data.enabled
        zones = data.zones
    }

    Tenant(Tenant tenant) {
        id = tenant.id
        name = tenant.name
        description = tenant.description
        enabled = tenant.enabled
        zones = tenant.zones
    }


    @Override
    public String toString() {
        return "Tenant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", enabled='" + enabled + '\'' +
                '}';
    }
}
