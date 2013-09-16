package asgard

import com.paypal.asgard.OpenStackRESTService
import com.paypal.asgard.QuantumService
import com.paypal.asgard.TenantService
import com.paypal.asgard.model.*
import grails.converters.JSON
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.converters.configuration.ConvertersConfigurationHolder
import org.codehaus.groovy.grails.web.converters.configuration.DefaultConverterConfiguration
import org.codehaus.groovy.grails.web.converters.marshaller.json.CollectionMarshaller
import org.codehaus.groovy.grails.web.converters.marshaller.json.MapMarshaller
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(QuantumService)

class QuantumServiceTest extends GMockTestCase {

    static final QUANTUM = 'network'
    static final String PREFIX = '/v2.0'

    static final tenant1 = [
            id: 'tenantId1',
            name: 'tenantName1',
            enabled: 'true'
    ]

    static final tenant2 = [
            id: 'tenantId2',
            name: 'tenantName2',
            enabled: 'true'
    ]

    static final tenants = [tenantId1: new Tenant(tenant1), tenantId2: new Tenant(tenant2)]

    static final subnet1 = [
            id: 'subnetId1',
            allocation_pools: [[start: '10.0.0.1', end: '10.0.0.5'], [start: '10.0.0.6', end: '10.0.0.10']],
            dns_nameservers: [],
            network_id: 'networkId1',
            name: 'subnetName1',
            enable_dhcp: true,
            tenant_id: tenant1.id,
            gateway_ip: '10.0.0.254',
            ip_version: 4,
            host_routers: [],
            cidr: '10.0.0.0/24'
    ]

    static final subnet2 = [
            id: 'subnetId2',
            allocation_pools: [[start: '10.0.2.1', end: '10.0.2.5'], [start: '10.0.2.6', end: '10.0.2.10']],
            dns_nameservers: [],
            network_id: 'networkId2',
            name: 'subnetName2',
            enable_dhcp: true,
            tenant_id: tenant2.id    ,
            gateway_ip: '10.0.2.254',
            ip_version: 4,
            host_routers: [],
            cidr: '10.0.2.0/24'
    ]

    static final subnets = [subnets: [subnet1, subnet2]]

    static final network1 = [
                    id: 'networkId1',
                    shared: false,
                    admin_state_up: true,
                    'router:external': true,
                    'provider:network_type': 'local',
                    status: 'ACTIVE',
                    'provider:segmentation_id': null,
                    name: 'networkName1',
                    'provider:physical_network': null,
                    tenant_id: tenant1.id,
                    subnets:[subnet1.id]
    ]

    static final network2 = [
                    id: 'networkId2',
                    shared: false,
                    admin_state_up: true,
                    'router:external': true,
                    'provider:network_type': 'local',
                    status: 'ACTIVE',
                    'provider:segmentation_id': null,
                    name: 'networkName2',
                    'provider:physical_network': null,
                    tenant_id: tenant2.id,
                    subnets:[subnet2.id]
    ]

    static final networks = [networks: [network1, network2]]


    static final router1 = [
                    id: 'routerId1',
                    admin_state_up: true,
                    routers: [],
                    external_gateway_info: [network_id: network1.id, enable_snat: true],
                    status: 'ACTIVE',
                    name: 'routerName1',
                    tenant_id: tenant1.id
    ]

    static final router2 = [
                    id: 'routerId2',
                    admin_state_up: true,
                    routers: [],
                    external_gateway_info: [network_id: network2.id, enable_snat: true],
                    status: 'ACTIVE',
                    name: 'routerName2',
                    tenant_id: tenant2.id
    ]

    static final routers = [routers: [router1, router2]]

    static final port1 = [
                    status: 'ACTIVE',
                    tenant_id: tenant1.id,
                    device_id: router1.id,
                    fixed_ips: [[subnet_id: subnet1.id, ip_address: '10.0.0.1']],
                    security_groups: [],
                    'binding:capabilities': [[port_filter: true]],
                    admin_state_up: true,
                    'binding:vif_type': 'ovs',
                    mac_address: 'mac1',
                    id: 'portId1',
                    network_id: network1.id,
                    name: 'portName1',
                    device_owner: 'network:router_interface',
                    'binding:host_id': null
    ]

    static final port2 = [
                    status: 'ACTIVE',
                    tenant_id: tenant2.id,
                    device_id: router2.id,
                    fixed_ips: [[subnet_id: subnet2.id, ip_address: '10.0.2.1']],
                    security_groups: [],
                    'binding:capabilities': [[port_filter: true]],
                    admin_state_up: true,
                    'binding:vif_type': 'ovs',
                    mac_address: 'mac2',
                    id: 'portId2',
                    network_id: network2.id,
                    name: 'portName2',
                    device_owner: 'network:dhcp',
                    'binding:host_id': null
    ]

    static final ports = [ports: [port1, port2]]

    static final iface = [id: 'ifId1', subnet_id: subnet1.id, port_id: port1.id, tenant_id: tenant1.id]

    @Before
    void setUp() {
        OpenStackRESTService openStackRESTService = mock(OpenStackRESTService)
        service.openStackRESTService = openStackRESTService
        service.openStackRESTService.QUANTUM.returns(QUANTUM).stub()

        service.openStackRESTService.get(QUANTUM, "$PREFIX/networks.json").returns(networks).stub()
        service.openStackRESTService.get(QUANTUM, "$PREFIX/subnets.json").returns(subnets).stub()
        service.openStackRESTService.get(QUANTUM, "$PREFIX/ports.json").returns(ports).stub()
        service.openStackRESTService.get(QUANTUM, "$PREFIX/routers.json").returns(routers).stub()

        TenantService tenantService = mock(TenantService)
        service.tenantService = tenantService
        service.tenantService.tenantsMap.returns(tenants).stub()

        // make JSON magic to work
        DefaultConverterConfiguration<JSON> defaultConverterConfig = new  DefaultConverterConfiguration<JSON>()
        defaultConverterConfig.registerObjectMarshaller(new CollectionMarshaller())
        defaultConverterConfig.registerObjectMarshaller(new MapMarshaller())

        ConvertersConfigurationHolder.setTheadLocalConverterConfiguration(JSON.class, defaultConverterConfig);
    }

    def testGetNetworkList() {
        def net1 = new Network(network1)
        net1.project = new Tenant(tenant1)
        net1.subnets << new Subnet(subnet1)

        def net2 = new Network(network2)
        net2.project = new Tenant(tenant2)
        net2.subnets << new Subnet(subnet2)

        play {
            assertEquals([net1, net2], service.getNetworkList())
        }
    }

    def testGetNetworkById() {
        service.openStackRESTService.get(QUANTUM, "$PREFIX/networks/${network1.id}").returns([network: network1]).times(1)
        service.tenantService.getTenantById(tenant1.id).returns(new Tenant(tenant1)).times(1)

        def net1 = new Network(network1)
        net1.project = new Tenant(tenant1)
        net1.subnets << new Subnet(subnet1)

        play {
            assertEquals(net1, service.getNetworkById(network1.id))
        }

    }

    def testGetNetworksByProjectId() {
        def net1 = new Network(network1)
        net1.project = new Tenant(tenant1)
        net1.subnets << new Subnet(subnet1)

        play {
            assertEquals([net1], service.getNetworksByProject(tenant1.id))
        }

    }

    def testGetSubnetList() {
        play {
            assertEquals([new Subnet(subnet1), new Subnet(subnet2)], service.getSubnetList())
        }
    }

    def testGetSubnetById() {
        service.openStackRESTService.get(QUANTUM, "$PREFIX/subnets/${subnet1.id}").returns([subnet: subnet1]).times(1)

        play {
            assertEquals(new Subnet(subnet1), service.getSubnetById(subnet1.id))
        }
    }

    def testGetSubnetMap() {
        play {
            assertEquals([subnetId1: new Subnet(subnet1), subnetId2: new Subnet(subnet2)], service.getSubnetMap())
        }
    }

    def testGetPortList() {
        play {
            assertEquals([new Port(port1), new Port(port2)], service.getPortList())
        }
    }

    def testGetPortById() {
        service.openStackRESTService.get(QUANTUM, "$PREFIX/ports/${port1.id}").returns([port: port1]).times(1)

        play {
            assertEquals(new Port(port1), service.getPortById(port1.id))
        }
    }

    def testGetPortsByNetworkId() {
        play {
            assertEquals([new Port(port1)], service.getPortsByNetworkId(network1.id))
        }
    }

    def testGetPortsByRouterId() {
        play {
            assertEquals([new Port(port2)], service.getPortsByRouterId(router2.id))
        }
    }

    def testCreateNetwork() {
        def net1 = new Network(network1)
        def post = [name: network1.name, admin_state_up: network1.admin_state_up, tenant_id: network1.tenant_id, 'router:external': network1['router:external'], shared: network1.shared]

        service.openStackRESTService.post(QUANTUM, "$PREFIX/networks", [network: post]).returns([network: network1]).times(1)

        play {
            assertEquals(net1, service.createNetwork(net1))
        }

    }

    def testUpdateNetwork() {
        def net1 = new Network(network1)
        def post = [name: network1.name, admin_state_up: network1.admin_state_up, 'router:external': network1['router:external'], shared: network1.shared]

        service.openStackRESTService.put(QUANTUM, "$PREFIX/networks/${network1.id}.json", null, [network: post]).returns([network: network1]).times(1)

        play {
            assertEquals(net1, service.updateNetwork(net1))
        }

    }

    def testCreateSubnet() {
        def sub1 = new Subnet(subnet1)
        def post = [
                name: subnet1.name,
                enable_dhcp: subnet1.enable_dhcp,
                network_id: subnet1.network_id,
                dns_nameservers: subnet1.dns_nameservers,
                allocation_pools: subnet1.allocation_pools,
                gateway_ip: subnet1.gateway_ip,
                ip_version: subnet1.ip_version,
                tenant_id: subnet1.tenant_id,
                host_routes: null,
                cidr: subnet1.cidr
        ]

        service.openStackRESTService.post(QUANTUM, "$PREFIX/subnets", [subnet: post]).returns([subnet: subnet1]).times(1)

        play {
            assertEquals(sub1, service.createSubnet(sub1))
        }

    }

    def testUpdateSubnet() {
        def sub1 = new Subnet(subnet1)
        def post = [
                name: subnet1.name,
                enable_dhcp: subnet1.enable_dhcp,
                dns_nameservers: subnet1.dns_nameservers,
                gateway_ip: subnet1.gateway_ip,
                host_routes: null
        ]

        service.openStackRESTService.put(QUANTUM, "$PREFIX/subnets/${subnet1.id}.json", null, [subnet: post]).returns([subnet: subnet1]).times(1)

        play {
            assertEquals(sub1, service.updateSubnet(sub1))
        }

    }

    def testCreatePort() {
        def post = [network_id: port1.network_id, fixed_ips: port1.fixed_ips]

        service.openStackRESTService.post(QUANTUM, "$PREFIX/ports", [port: post]).returns([port: port1]).times(1)

        def newPort = new Port()
        newPort.networkId = port1.network_id
        newPort.fixedIps = port1.fixed_ips

        // there are two ways to create port

        def post2 = [network_id: port1.network_id, device_owner: port1.device_owner, device_id: port1.device_id, name: port1.name ,admin_state_up: port1.admin_state_up]

        service.openStackRESTService.post(QUANTUM, "$PREFIX/ports", [port: post2]).returns([port: port1]).times(1)

        def newPort2 = new Port()
        newPort2.networkId = port1.network_id
        newPort2.name =  port1.name
        newPort2.deviceId = port1.device_id
        newPort2.deviceOwner = port1.device_owner
        newPort2.adminStateUp = port1.admin_state_up

        play {
            assertEquals(new Port(port1), service.createPort(newPort))
            assertEquals(new Port(port1), service.createPort(newPort2))
        }

    }

    def testUpdatePort() {
        def put = [device_owner: port1.device_owner, device_id: port1.device_id, name: port1.name ,admin_state_up: port1.admin_state_up]

        service.openStackRESTService.put(QUANTUM, "$PREFIX/ports/${port1.id}.json", null, [port: put]).returns([port: port1]).times(1)

        def newPort = new Port()
        newPort.id = port1.id
        newPort.name =  port1.name
        newPort.deviceId = port1.device_id
        newPort.deviceOwner = port1.device_owner
        newPort.adminStateUp = port1.admin_state_up

        play {
            assertEquals(new Port(port1), service.updatePort(newPort))
        }

    }

    def testGetRouterList() {
        def rt1 = new Router(router1)
        rt1.externalGatewayInfo.networkName = network1.name

        def rt2 = new Router(router2)
        rt2.externalGatewayInfo.networkName = network2.name

        play {
            assertEquals([rt1, rt2], service.getRoutersList())
        }
    }

    def testGetRouterById() {
        service.openStackRESTService.get(QUANTUM, "$PREFIX/routers/$router1.id").returns([router: router1]).times(1)

        def rt1 = new Router(router1)
        rt1.externalGatewayInfo.networkName = network1.name
        rt1.ports << new Port(port1)

        play {
            assertEquals(rt1, service.getRouterById(router1.id))
        }

    }

    def testGetNetworkMap() {
        def net1 = new Network(network1)
        net1.project = new Tenant(tenant1)
        net1.subnets << new Subnet(subnet1)

        def net2 = new Network(network2)
        net2.project = new Tenant(tenant2)
        net2.subnets << new Subnet(subnet2)

        play {
            assertEquals([networkId1: net1, networkId2: net2], service.getNetworkMap())
        }
    }

    def testCreateRouter() {
        service.openStackRESTService.post(QUANTUM, "$PREFIX/routers", [router: [name: router1.name]]).returns([router: router1]).times(1)

        def newRouter = new Router()
        newRouter.name = router1.name

        play {
            assertEquals(new Router(router1), service.createRouter(newRouter))
        }

    }

    def testUpdateRouter() {
        service.openStackRESTService.put(QUANTUM, "$PREFIX/routers/${router1.id}.json", null, [router: [external_gateway_info : [network_id: router1.external_gateway_info.network_id]]]).returns([router: router1]).times(1)

        def newRouter = new Router()
        newRouter.id = router1.id
        newRouter.externalGatewayInfo = new ExternalGatewayInfo()
        newRouter.externalGatewayInfo.networkId = router1.external_gateway_info.network_id

        play {
            assertEquals(new Router(router1), service.updateRouter(newRouter))
        }

    }

    def testAddRouterInterfaceBySubnet() {
        service.openStackRESTService.put(QUANTUM, "$PREFIX/routers/$router1.id/add_router_interface", null, [subnet_id: subnet1.id]).returns(iface).times(1)

        play {
            assertEquals(new JSON(iface).toString(), service.addRouterInterfaceBySubnet(router1.id, subnet1.id).toString())
        }
    }

    def testAddRouterInterfaceByPort() {
        service.openStackRESTService.put(QUANTUM, "$PREFIX/routers/$router1.id/add_router_interface", null, [port_id: port1.id]).returns(iface).times(1)

        play {
            assertEquals(new JSON(iface).toString(), service.addRouterInterfaceByPort(router1.id, port1.id).toString())
        }
    }

    def testRemoveRouterInterfaceByPort() {
        service.openStackRESTService.put(QUANTUM, "$PREFIX/routers/$router1.id/remove_router_interface", null, [port_id: port1.id]).returns(iface).times(1)

        play {
            assertEquals(new JSON(iface).toString(), service.removeRouterInterface(router1.id, port1.id).toString())
        }
    }

    void testDeleteNetwork() {
        service.openStackRESTService.delete(QUANTUM, "$PREFIX/networks/networkId1").returns(null).times(1)

        play {
            assertNull(service.deleteNetwork('networkId1'))
        }
    }

    void testDeleteSubnet() {
        service.openStackRESTService.delete(QUANTUM, "$PREFIX/subnets/subnetId1").returns(null).times(1)

        play {
            assertNull(service.deleteSubnet('subnetId1'))
        }
    }

    void testDeletePort() {
        service.openStackRESTService.delete(QUANTUM, "$PREFIX/ports/portId1").returns(null).times(1)

        play {
            assertNull(service.deletePort('portId1'))
        }
    }

    void testDeleteRouter() {
        service.openStackRESTService.delete(QUANTUM, "$PREFIX/routers/routerId1").returns(null).times(1)

        play {
            assertNull(service.deleteRouter('routerId1'))
        }
    }

    def testIsServiceEnabled() {
        service.openStackRESTService.isServiceEnabled(QUANTUM).returns(true).times(1)

        play {
            assertTrue(service.isEnabled())
        }
    }
}
