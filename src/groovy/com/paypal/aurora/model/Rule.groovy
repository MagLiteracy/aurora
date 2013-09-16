package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Rule {
    String id
    String ipProtocol
    String fromPort
    String toPort
    String source
    String parentGroupId

    Rule() {

    }

    Rule(Map data) {
        id = data.id
        ipProtocol = data.ip_protocol
        fromPort = data.from_port
        toPort = data.to_port
        source = data.ip_range?.cidr?: data?.group.name
        parentGroupId = data.parent_group_id
    }


    @Override
    public String toString() {
        return "Rule{" +
                "id='" + id + '\'' +
                ", ipProtocol='" + ipProtocol + '\'' +
                ", fromPort='" + fromPort + '\'' +
                ", toPort='" + toPort + '\'' +
                ", source='" + source + '\'' +
                ", parentGroupId='" + parentGroupId + '\'' +
                '}';
    }

}
