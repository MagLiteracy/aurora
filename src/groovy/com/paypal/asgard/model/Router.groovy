package com.paypal.asgard.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Router {
    String id
    String name
    Boolean adminState
    String tenantId
    ExternalGatewayInfo externalGatewayInfo
    String status
    List<Port> ports = []

    Router(Map data) {
        id = data.id
        name = data.name
        adminState = data.admin_state_up
        tenantId = data.tenant_id
        externalGatewayInfo = data.external_gateway_info
        status = data.status
    }

    Router(){
    }


    @Override
    public String toString() {
        return "Router{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", adminState=" + adminState +
                ", tenantId='" + tenantId + '\'' +
                ", externalGatewayInfo=" + externalGatewayInfo +
                ", status='" + status + '\'' +
                '}';
    }
}
