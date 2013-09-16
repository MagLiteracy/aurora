package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class SecurityGroup {
    String id
    String name
    String description
    List<Rule> rules

    SecurityGroup() {

    }

    SecurityGroup(Map data) {
        id = data.id
        name = data.name
        description = data.description
        rules = []
        for (rule in data.rules) {
            rules << new Rule(rule)
        }
    }

    @Override
    public String toString() {
        return "SecurityGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", rules=" + rules +
                '}';
    }
}
