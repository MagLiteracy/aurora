package com.paypal.asgard.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Role {
    String name
    String id

    Role() {
    }

    Role(def role) {
        def firstRole

        if (role instanceof List) {
            firstRole = role == [] ? null: role[0]
        } else {
            firstRole = role
        }

        name = firstRole?.name
        id = firstRole?.id
    }

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
