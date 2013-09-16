package com.paypal.asgard.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Network {
    String status
    List<String> subnetsId
    List<Subnet> subnets = []
    String name
    Boolean adminStateUp
    String projectId
    Tenant project
    String id
    Boolean shared
    Boolean external

    Network() {

    }

    Network(def data) {
        status = data.status
        name = data.name
        adminStateUp = data.admin_state_up
        id = data.id
        shared = data.shared
        external = data.'router:external'
        projectId = data.tenant_id
        subnetsId = data.subnets
    }


    @Override
    public String toString() {
        return "Network{" +
                "status='" + status + '\'' +
                ", subnetsId=" + subnetsId +
                ", subnets=" + subnets +
                ", name='" + name + '\'' +
                ", adminStateUp=" + adminStateUp +
                ", projectId='" + projectId + '\'' +
                ", project=" + project +
                ", id='" + id + '\'' +
                ", shared=" + shared +
                ", external=" + external +
                '}';
    }
}
