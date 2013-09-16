package com.paypal.asgard.model

import groovy.transform.EqualsAndHashCode


@EqualsAndHashCode
class HostRoute {
    String nexthop
    String destination

    HostRoute(String destination, String nexthop) {
        this.destination = destination
        this.nexthop = nexthop
    }

    HostRoute() {

    }


    @Override
    public String toString() {
        return "HostRoute{" +
                "nexthop='" + nexthop + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}
