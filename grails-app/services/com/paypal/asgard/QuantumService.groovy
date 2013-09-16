package com.paypal.asgard

import com.paypal.asgard.model.*
import grails.converters.JSON

class QuantumService {
    static final String PREFIX = '/v2.0'

    def openStackRESTService
    def tenantService

    boolean isEnabled() {
        openStackRESTService.isServiceEnabled(openStackRESTService.QUANTUM)
    }

    List<Network> getNetworkList() {
        List<Network> networks = []
        Map<String, Tenant> tenants = tenantService.tenantsMap
        Map<String, Subnet> subnets = subnetMap
        def networksResp = openStackRESTService.get(openStackRESTService.QUANTUM, "$PREFIX/networks.json")
        networksResp.networks.each{
            def network = new Network(it)
            network.project = tenants[network.projectId]
            network.subnetsId.each {network.subnets << subnets[it]}
            networks << network
        }
        return networks
    }

    Network getNetworkById(String id) {
        Network network = new Network(
            openStackRESTService.get(openStackRESTService.QUANTUM, "$PREFIX/networks/$id").network
        )
        Map<String, Subnet> subnets = subnetMap
        network.subnetsId.each {network.subnets << subnets[it]}
        network.project = tenantService.getTenantById(network.projectId)
        return network
    }

    List<Network> getNetworksByProject(String id) {
        networkList.findAll {it.projectId == id}
    }

    List<Subnet> getSubnetList() {
        List<Subnet> subnets = []
        def subnetsResp = openStackRESTService.get(openStackRESTService.QUANTUM, "$PREFIX/subnets.json")
        subnetsResp.subnets.each{
            subnets << new Subnet(it)
        }
        return subnets
    }

    Subnet getSubnetById(String id) {
        new Subnet(
            openStackRESTService.get(openStackRESTService.QUANTUM, "$PREFIX/subnets/$id").subnet
        )
    }

    Map<String, Subnet> getSubnetMap() {
        Map<String, Subnet> result = [:]
        subnetList.each {
            result.put(it.id, it)
        }
        return result
    }

    List<Port> getPortList() {
        List<Port> ports = []
        def portsResp = openStackRESTService.get(openStackRESTService.QUANTUM, "$PREFIX/ports.json")
        portsResp.ports.each{
            ports << new Port(it)
        }
        return ports
    }

    List<Port> getPortsByNetworkId(String id) {
        return portList.findAll {it.networkId==id}
    }

    List<Port> getPortsByRouterId(String id) {
        return portList.findAll {it.deviceId ==id}
    }

    Port getPortById(String id) {
        new Port(
            openStackRESTService.get(openStackRESTService.QUANTUM, "$PREFIX/ports/$id").port
        )
    }

    void deleteNetwork(String id) {
        openStackRESTService.delete(openStackRESTService.QUANTUM, "$PREFIX/networks/$id")
    }

    void deleteSubnet(String id) {
        openStackRESTService.delete(openStackRESTService.QUANTUM, "$PREFIX/subnets/$id")
    }

    void deletePort(String id) {
        openStackRESTService.delete(openStackRESTService.QUANTUM, "$PREFIX/ports/$id")
    }

    Network createNetwork(Network network) {
        new Network(openStackRESTService.post(openStackRESTService.QUANTUM, "$PREFIX/networks", [network: [
                name: network.name,
                admin_state_up: network.adminStateUp,
                tenant_id: network.projectId,
                'router:external': network.external,
                shared: network.shared
        ]]).network)
    }

    Network updateNetwork(Network network) {
        new Network(openStackRESTService.put(openStackRESTService.QUANTUM, "$PREFIX/networks/${network.id}.json", null, [network: [
                name: network.name,
                admin_state_up: network.adminStateUp,
                'router:external': network.external,
                shared: network.shared
        ]]).network)
    }

    Subnet createSubnet(Subnet subnet) {
        new Subnet(openStackRESTService.post(openStackRESTService.QUANTUM, "$PREFIX/subnets", [subnet: [
                name: subnet.name,
                enable_dhcp: subnet.enableDhcp,
                network_id: subnet.networkId,
                dns_nameservers: subnet.dnsNameservers,
                allocation_pools: subnet.allocationPools,
                gateway_ip: subnet.gatewayIp,
                ip_version: subnet.ipVersion,
                tenant_id: subnet.tenantId,
                host_routes: subnet.hostRoutes,
                cidr: subnet.cidr
        ]]).subnet)
    }

    Subnet updateSubnet(Subnet subnet) {
        new Subnet(openStackRESTService.put(openStackRESTService.QUANTUM, "$PREFIX/subnets/${subnet.id}.json", null, [subnet: [
                name: subnet.name,
                enable_dhcp: subnet.enableDhcp,
                dns_nameservers: subnet.dnsNameservers,
                gateway_ip: subnet.gatewayIp,
                host_routes: subnet.hostRoutes
        ]]).subnet)
    }

    Port createPort(Port port) {
        def json = [port: [

                network_id: port.networkId
        ]]
        if (port.fixedIps != null && !port.fixedIps.isEmpty()) {
            json['port'] << [fixed_ips:[]]
            for (FixedIp fixedIp : port.fixedIps) {
                json['port']['fixed_ips'] << [subnet_id: fixedIp.subnetId, ip_address: fixedIp.ipAddress]
            }
        } else {
            json['port'] << [device_owner: port.deviceOwner,
                    device_id: port.deviceId,
                    name: port.name,
                    admin_state_up: port.adminStateUp,]
        }
        new Port(openStackRESTService.post(openStackRESTService.QUANTUM, "$PREFIX/ports", json).port)
    }

    Port updatePort(Port port) {
        new Port(openStackRESTService.put(openStackRESTService.QUANTUM, "$PREFIX/ports/${port.id}.json", null, [port: [
                device_owner: port.deviceOwner,
                device_id: port.deviceId,
                name: port.name,
                admin_state_up: port.adminStateUp
        ]]).port)
    }

    List<Router> getRoutersList() {
        List<Router> routers = []
        Map<String, Network> networks = networkMap
        def routersResp = openStackRESTService.get(openStackRESTService.QUANTUM, "$PREFIX/routers.json")
        routersResp.routers.each{
            def router = new Router(it)
            if(router.externalGatewayInfo != null) {
                router.externalGatewayInfo.networkName = networks[router.externalGatewayInfo.networkId].name
            }
            routers << router
        }
        return routers
    }

    Router getRouterById(String id) {
        Map<String, Network> networks = networkMap
        def router = new Router(openStackRESTService.get(openStackRESTService.QUANTUM, "$PREFIX/routers/$id").router)
        if(router.externalGatewayInfo != null) {
            router.externalGatewayInfo.networkName = networks[router.externalGatewayInfo.networkId].name
        }
        router.ports = getPortsByRouterId(id)
        return router
    }

    Map<String, Network> getNetworkMap() {
        Map<String, Network> result = [:]
        networkList.each {
            result.put(it.id, it)
        }
        return result
    }

    Router updateRouter(Router router) {
        def json = [router : [external_gateway_info : null]]
        if(router.externalGatewayInfo != null) {
            json['router'] = [external_gateway_info : [network_id: router.externalGatewayInfo.networkId]]
        }
        new Router(openStackRESTService.put(openStackRESTService.QUANTUM, "$PREFIX/routers/${router.id}.json", null,
                json).router)
    }

    Router createRouter(Router router) {
        new Router(openStackRESTService.post(openStackRESTService.QUANTUM, "$PREFIX/routers", [router: [
                name: router.name,
        ]]).router)
    }

    void deleteRouter(String id) {
        openStackRESTService.delete(openStackRESTService.QUANTUM, "$PREFIX/routers/$id")
    }

    JSON addRouterInterfaceBySubnet(String routerId, String subnetId) {
        new JSON(openStackRESTService.put(openStackRESTService.QUANTUM, "$PREFIX/routers/${routerId}/add_router_interface",
                null, [subnet_id: subnetId]))
    }

    JSON addRouterInterfaceByPort(String routerId, String portId) {
        new JSON(openStackRESTService.put(openStackRESTService.QUANTUM, "$PREFIX/routers/${routerId}/add_router_interface",
                null, [port_id: portId]))
    }

    JSON removeRouterInterface(String routerId, String portId) {
        new JSON(openStackRESTService.put(openStackRESTService.QUANTUM, "$PREFIX/routers/${routerId}/remove_router_interface",
                null, [port_id: portId]))
    }
}
