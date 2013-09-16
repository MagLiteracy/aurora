package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class OpenStackUser {
    String name
    String email
    String id
    String tenantId
    boolean enabled

    OpenStackUser(){}

    OpenStackUser(def user) {
        name = user.name
        email = user.email
        id = user.id
        tenantId = user.tenantId
        enabled = user.enabled
    }


    @Override
    public String toString() {
        return "OpenStackUser{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
