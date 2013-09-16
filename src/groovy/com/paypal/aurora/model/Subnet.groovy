package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Subnet {

    String name
    String id
    String networkId
    String tenantId
    String gatewayIp
    Integer ipVersion
    String cidr
    Boolean enableDhcp
    List<Range> allocationPools = []
    List<String> dnsNameservers = []
    List<HostRoute> hostRoutes = []

    Subnet() {

    }

    Subnet(Map data) {
        name = data.name
        id = data.id
        networkId = data.network_id
        tenantId = data.tenant_id
        gatewayIp = data.gateway_ip
        ipVersion = data.ip_version
        cidr = data.cidr
        enableDhcp = data.enable_dhcp
        dnsNameservers = data.dns_nameservers
        hostRoutes = data.host_routes
        allocationPools = data.allocation_pools
    }


    @Override
    public String toString() {
        return "Subnet{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", networkId='" + networkId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", gatewayIp='" + gatewayIp + '\'' +
                ", ipVersion=" + ipVersion +
                ", cidr='" + cidr + '\'' +
                ", enableDhcp=" + enableDhcp +
                ", allocationPools=" + allocationPools +
                ", dnsNameservers=" + dnsNameservers +
                ", hostRoutes=" + hostRoutes +
                '}';
    }
}
