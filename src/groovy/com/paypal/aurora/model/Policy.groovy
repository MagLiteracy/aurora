package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Policy {
    String name
    String rule

    Policy() {

    }

    Policy(Map data) {
        name = data.name
        rule = data.rule
    }


    @Override
    public String toString() {
        return "Policy{" +
                "name='" + name + '\'' +
                ", rule='" + rule + '\'' +
                '}';
    }
}
