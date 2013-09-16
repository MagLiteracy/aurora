package asgard

import com.paypal.asgard.NetworkService
import com.paypal.asgard.OpenStackRESTService
import com.paypal.asgard.QuantumDNSService
import com.paypal.asgard.SessionStorageService
import com.paypal.asgard.model.ExternalFloatingIp
import com.paypal.asgard.model.FloatingIp
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(NetworkService)
class NetworkServiceTest extends GMockTestCase {

    static final FLOATING_IPS = 'os-floating-ips'
    static final PREFIX = 'v2.0'
    static final POOLS = "os-floating-ip-pools"
    static final EXTERNAL_FLOATING_IPS = 'external_floating_ips'
    static final QUANTUM_FLIP_PATH = 'floatingips'
    static final NOVA = 'compute'
    static final QUANTUM = 'network'

    static final flip1 = [id: 'flipId1', pool: 'pool1', ip: '127.0.1.0', fixed_ip: null, instance_id: 'instanceId1']
    static final flip2 = [id: 'flipId2', pool: 'pool2', ip: '127.0.2.0', fixed_ip: null, instance_id: null]

    static final eflip1 = [id: 'eflipId1', router_id: 'routerId1', tenant_id: 'tenantId1', port_id: 'portId1', fixed_network_id: 'fixedNetworkId1', fixed_ip_address: 'fixedIp1', floating_ip_address: 'floatingIp1']
    static final eflip2 = [id: 'eflipId2', router_id: 'routerId2', tenant_id: 'tenantId2', port_id: 'portId2', fixed_network_id: 'fixedNetworkId2', fixed_ip_address: 'fixedIp2', floating_ip_address: 'floatingIp2']

    static final fqdn1 = 'foo1.bar.paypal.com'
    static final fqdn2 = 'foo2.bar.paypal.com'
    static final zone = 'zone'

    @Before
    void setUp() {
        OpenStackRESTService openStackRESTService = mock(OpenStackRESTService);
        service.openStackRESTService = openStackRESTService
        service.openStackRESTService.NOVA.returns(NOVA).stub()
        service.openStackRESTService.QUANTUM.returns(QUANTUM).stub()

        service.openStackRESTService.get(NOVA, FLOATING_IPS).returns([floating_ips: [flip1, flip2]]).stub()
        service.openStackRESTService.get(QUANTUM, "$PREFIX/$QUANTUM_FLIP_PATH").returns([floatingips: [eflip1, eflip2]]).stub()

        SessionStorageService sessionStorageService = mock(SessionStorageService)
        service.sessionStorageService = sessionStorageService

        service.quantumDNSService = mock(QuantumDNSService)
    }

    def testGetFloatingIpsWithOutQuantumDNSService() {
        service.quantumDNSService.isEnabled().returns(false).stub()

        play {
            assertEquals([new FloatingIp(flip1), new FloatingIp(flip2)], service.getFloatingIps())
        }

    }

    def testGetFloatingIpsWithQuantumDNSService() {
        service.quantumDNSService.isEnabled().returns(true).stub()
        service.quantumDNSService.getFqdnByIp(flip1.ip).returns(fqdn1).times(1)
        service.quantumDNSService.getFqdnByIp(flip2.ip).returns(fqdn2).times(1)
        FloatingIp flip1 = new FloatingIp(flip1);
        FloatingIp flip2 = new FloatingIp(flip2);
        flip1.fqdn = fqdn1
        flip2.fqdn = fqdn2

        play {
            assertEquals([flip1,flip2], service.getFloatingIps())
        }

    }

    def testGetUnassignedFloatingIps() {
        service.quantumDNSService.isEnabled().returns(false).stub()

        play {
            assertEquals([new FloatingIp(flip2)], service.getUnassignedFloatingIps())
        }

    }

    def testGetExternalFloatingIps() {
        play {
            assertEquals([new ExternalFloatingIp(eflip1), new ExternalFloatingIp(eflip2)], service.getExternalFloatingIps())
        }
    }

    def testGetExternalFloatingIpsMap() {

        def map = [:]
        map[eflip1.fixed_ip_address] = eflip1.floating_ip_address
        map[eflip2.fixed_ip_address] = eflip2.floating_ip_address

        play {
            assertEquals(map, service.getExternalFloatingIpsMap())
        }
    }

    def testGetFloatingIpsForInstance() {
        service.quantumDNSService.isEnabled().returns(false).stub()

        play {
            assertEquals([new FloatingIp(flip1)], service.getFloatingIpsForInstance(flip1.instance_id))
        }

    }

    def testGetFloatingIpById() {
        service.openStackRESTService.get(NOVA, "$FLOATING_IPS/$flip1.id").returns([floating_ip: flip1])

        play {
            assertEquals(new FloatingIp(flip1), service.getFloatingIpById(flip1.id))
        }

    }

    def testGetFloatingIpPools(){
        service.openStackRESTService.get(NOVA, POOLS).returns([floating_ip_pools: [[name: flip1.pool]]])
        play {
            assertEquals([[name: flip1.pool]], service.floatingIpPools)
        }
    }

    def testAllocateFloatingIpWithOutQuantumDNSService() {
        service.openStackRESTService.post(NOVA, FLOATING_IPS, [pool: flip1.pool]).returns([floating_ip: flip1]).times(1)
        service.quantumDNSService.isEnabled().returns(false).stub()

        play {
            assertEquals([floating_ip: flip1], service.allocateFloatingIp(flip1.pool))
        }
    }

    def testAllocateFloatingIpWithQuantumDNSService() {
        service.openStackRESTService.post(NOVA, FLOATING_IPS, [pool: flip1.pool]).returns([floating_ip: flip1]).times(1)
        service.quantumDNSService.isEnabled().returns(true).stub()
        service.quantumDNSService.addDnsRecord(null, flip1.ip, null).returns().times(1)

        play {
            assertEquals([floating_ip: flip1], service.allocateFloatingIp(flip1.pool))
        }
    }

    def testReleaseFloatingIpWithOutQuantumDNSService() {
        service.openStackRESTService.delete(NOVA, "$FLOATING_IPS/$flip1.ip").returns(null).times(1)
        service.quantumDNSService.isEnabled().returns(false).stub()

        play {
            assertNull(service.releaseFloatingIp(flip1.ip))
        }
    }

    def testReleaseFloatingIpWithQuantumDNSService() {
        service.openStackRESTService.delete(NOVA, "$FLOATING_IPS/$flip1.ip").returns(null).times(1)
        service.quantumDNSService.isEnabled().returns(true).stub()
        service.quantumDNSService.deleteDnsRecordByIP(flip1.ip, zone).returns().times(1)
        service.sessionStorageService.getTenant().returns(zone: zone).times(1)

        play {
            assertNull(service.releaseFloatingIp(flip1.ip))
        }
    }

    def testAssociateFloatingIp() {
        service.openStackRESTService.post(NOVA, "servers/$flip1.instance_id/action", [addFloatingIp: [address: flip1.ip]]).returns([]).times(1)
        play {
            assertEquals([], service.associateFloatingIp(flip1.instance_id, flip1.ip))
        }
    }

    def testDisassociateFloatingIp() {
        service.openStackRESTService.post(NOVA, "servers/$flip1.instance_id/action", [removeFloatingIp: [address: flip1.ip]]).returns([]).times(1)
        play {
            assertEquals([], service.disassociateFloatingIp(flip1.instance_id, flip1.ip))
        }
    }

    def testIsUseExternalFLIP() {
        service.sessionStorageService.isFlagEnabled(EXTERNAL_FLOATING_IPS).returns(true).times(1)

        play {
            assertTrue(service.isUseExternalFLIP())
        }
    }

}
