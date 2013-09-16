package aurora

import com.paypal.aurora.*
import com.paypal.aurora.model.ExternalFloatingIp
import com.paypal.aurora.model.FloatingIp
import com.paypal.aurora.model.Instance
import com.paypal.aurora.model.RestResponse
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(InstanceService)
class InstanceServiceTest extends GMockTestCase {

    static final bodyForInstanceCreation = [server: [name: 'temp',
            flavorRef: '0c8039da-259d-47da-b9b7-dbdea4af1f05',
            imageRef: '09fc3cbe-3ed3-4b21-893c-3f860be9e9e1',
            min_count: 1, max_count: 1, key_name: 'fm1kp',
            security_groups: [[name: 'default']],
            availability_zone: 'local-lab1',
            block_device_mapping: [[device_name:'deviceName', volume_size:'',
                    volume_id:'3fbbef8b-0ae4-4b8b-9281-6edcc52739f5', delete_on_termination:'1']],
            user_data:'a2lsbCAxMjM0'],
            networks: [['uuid': QuantumServiceTest.network1]],

            ]

    static final bodyForInstanceCreation2 = [server: [name: 'temp',
            flavorRef: '0c8039da-259d-47da-b9b7-dbdea4af1f05',
            imageRef: '09fc3cbe-3ed3-4b21-893c-3f860be9e9e1',
            min_count: 1, max_count: 1, key_name: 'fm1kp',
            security_groups: [[name: 'default']], availability_zone: 'local-lab1',
            block_device_mapping: [[device_name:'deviceName', volume_size:'',
                    snapshot_id:'1234', delete_on_termination:'0']]]]
    static final instanceMapForCreation = [securityGroups: ['default'],
            count: '1',
            _securityGroups: ['', '', '', '', '', '', '', '', '', ''],
            snapshot: '09fc3cbe-3ed3-4b21-893c-3f860be9e9e1',
            flavor: '0c8039da-259d-47da-b9b7-dbdea4af1f05',
            image: '09fc3cbe-3ed3-4b21-893c-3f860be9e9e1',
            datacenter: 'local-lab1',
            _action_save: '',
            keypair: 'fm1kp',
            deviceName: 'deviceName',
            _deleteOnTerminate: '',
            name: 'temp',
            instanceSources: InstanceController.InstanceSources.IMAGE.toString(),
            volume: '3fbbef8b-0ae4-4b8b-9281-6edcc52739f5',
            customizationScript: 'kill 1234',
            volumeOptions: InstanceController.VolumeOptions.BOOT_FROM_VOLUME.toString(),
            action: 'save',
            controller: 'instance',
            networks: [QuantumServiceTest.network1],
            deleteOnTerminate: 'on']
    static final instanceMapForCreation2 = [securityGroups: 'default',
            count: '1',
            _securityGroups: ['', '', '', '', '', '', '', '', '', ''],
            snapshot: '09fc3cbe-3ed3-4b21-893c-3f860be9e9e1',
            flavor: '0c8039da-259d-47da-b9b7-dbdea4af1f05',
            image: '09fc3cbe-3ed3-4b21-893c-3f860be9e9e1',
            datacenter: 'local-lab1',
            _action_save: '',
            keypair: 'fm1kp',
            deviceName: 'deviceName',
            _deleteOnTerminate: '',
            name: 'temp',
            instanceSources: InstanceController.InstanceSources.IMAGE.toString(),
            volume: '3fbbef8b-0ae4-4b8b-9281-6edcc52739f5',
            customizationScript: '',
            volumeOptions: InstanceController.VolumeOptions.BOOT_FROM_SNAPSHOT.toString(),
            action: 'save',
            controller: 'instance',
            volumeSnapshot: '1234']
    static final NOVA = "compute"
    static final OKResponse = new RestResponse(200, "OK")
    static final instance1AsMap = [
            id: 'id1',
            name: 'name1',
            instanceSources: 'Image',
            status: 'Active',
            volumeOptions: 'Do not boot from volume',
            images: 'cirros_ClAz_SFMC_oiIT_oO4g_Uc71_0iJ1_3Wml',
            count: '1',
            flavor: [id: 'flavorId1', name: 'flavorName'],
            image: [id: 'imageId', name: 'imageName'],
            addresses: ['eth0': [[addr: '127.0.0.3']]]]
    static final instance2AsMap = [
            id: 'id2',
            name: 'name2',
            instanceSources: 'Image',
            status: 'Active',
            volumeOptions: 'Do not boot from volume',
            images: 'cirros_ClAz_SFMC_oiIT_oO4g_Uc71_0iJ1_3Wml',
            count: '1',
            flavor: [id: 'flavorId2', name: 'flavorName'],
            image: [id: 'imageId', name: 'imageName'],
            addresses: ['eth1': [[addr: '127.0.0.4']]]]
    static final instanceListAsMap = [servers: [instance1AsMap, instance2AsMap]]
    static final instances = [new Instance(instance1AsMap), new Instance(instance2AsMap)]
    static final String SHOW_ADMIN_CREDENTIALS = 'admin_login_credentials'
    static final String SHOW_USER_CREDENTIALS = 'user_login_credentials'
    static final String FQDN = 'fqdn'
    static final externalFloatingIpsMap = ['1.1.1.1': '8.8.8.8']
    static final floatingIpAsMap = [pool: 'pool', id: 'id', instance_id: instance1AsMap.id, ip: '8.8.8.8', fixed_ip: '1.1.1.1']
    static final FloatingIp floatingIp = new FloatingIp(floatingIpAsMap)

    @Before
    void setUp() {
        service.openStackRESTService = mock(OpenStackRESTService);
        service.openStackRESTService.NOVA.returns(NOVA).stub()
        service.networkService = mock(NetworkService)
        service.sessionStorageService = mock(SessionStorageService)
        service.sessionStorageService.isFlagEnabled(SHOW_USER_CREDENTIALS).returns(false).stub()
        service.sessionStorageService.isFlagEnabled(SHOW_ADMIN_CREDENTIALS).returns(true).stub()
        service.quantumDNSService = mock(QuantumDNSService)
        service.quantumDNSService.isEnabled().returns(true).stub()
    }


    public void testUpdate() {
        service.openStackRESTService.put(NOVA, "servers/$instance1AsMap.id", null, [server: [name: instance1AsMap.name]]).returns(OKResponse)
        play {
            assertEquals(OKResponse, service.update(instance1AsMap))
        }
    }


    void testResume() {
        service.openStackRESTService.post(NOVA, "servers/$instance1AsMap.id/action", '{"resume": null}').returns(OKResponse)
        play{
            assertEquals(OKResponse, service.resume(instance1AsMap.id))
        }
    }


    void testSuspend() {
        service.openStackRESTService.post(NOVA, "servers/$instance1AsMap.id/action", '{"suspend": null}').returns(OKResponse)
        play{
            assertEquals(OKResponse, service.suspend(instance1AsMap.id))
        }
    }


    void testPause() {
        service.openStackRESTService.post(NOVA, "servers/$instance1AsMap.id/action", '{"pause": null}').returns(OKResponse)
        play{
            assertEquals(OKResponse, service.pause(instance1AsMap.id))
        }
    }


    void testUnpause() {
        service.openStackRESTService.post(NOVA, "servers/$instance1AsMap.id/action", '{"unpause": null}').returns(OKResponse)
        play{
            assertEquals(OKResponse, service.unpause(instance1AsMap.id))
        }
    }


    void testCreateSnapshot() {
        service.openStackRESTService.post(NOVA, "servers/$instance1AsMap.id/action", [createImage: [name: instance1AsMap.name, metadata: [:]]]).returns(OKResponse)
        play{
            assertEquals(OKResponse, service.createSnapshot(instance1AsMap.id, instance1AsMap.name))
        }
    }


    void testGetLog() {
        int length = 35
        def resp = [output: "log string"]
        service.openStackRESTService.post(NOVA, "servers/$instance1AsMap.id/action", ['os-getConsoleOutput': [length: length]]).returns(resp)
        play{
            assertEquals(resp.output, service.getLog(instance1AsMap.id, length))
        }
    }


    void testGetVncUrl() {
        def resp = [console: [url: 'url']]
        service.openStackRESTService.post(NOVA, "servers/$instance1AsMap.id/action", ['os-getVNCConsole': ['type': 'novnc']]).returns(resp)
        play{
            assertEquals(resp.console.url, service.getVncUrl(instance1AsMap.id))
        }
    }


    void testReboot() {
        service.openStackRESTService.post(NOVA, "servers/$instance1AsMap.id/action", [reboot: [type: "SOFT"]]).returns(OKResponse)
        play{
            assertEquals(OKResponse, service.reboot(instance1AsMap.id))
        }
    }


    void testDeleteById() {
        service.openStackRESTService.delete(NOVA, "servers/$instance1AsMap.id").returns(OKResponse)
        play{
            assertEquals(OKResponse, service.deleteById(instance1AsMap.id))
        }
    }


    void testListAll() {
        service.openStackRESTService.get(NOVA, 'servers/detail').returns(instanceListAsMap)
        service.networkService.getFloatingIps().returns([floatingIp]).stub()
        service.networkService.isUseExternalFLIP().returns(false).times(1)

        play{
            assertArrayEquals(instances.toArray(), service.listAll(true).toArray())
        }
    }


    void testListAllUseExternalFLIP() {
        service.openStackRESTService.get(NOVA, 'servers/detail').returns(instanceListAsMap)
        service.networkService.getExternalFloatingIpsMap().returns(externalFloatingIpsMap).stub()
        service.networkService.isUseExternalFLIP().returns(true).times(1)

        play{
            assertArrayEquals(instances.toArray(), service.listAll(true).toArray())
        }
    }


    void testAllActiveInstances() {
        def map = instanceListAsMap
        List<Instance> instances = [] as LinkedList
        for (server in map.servers) {
            if("Active".equals(server.status)) {
                instances.push(new Instance(server))
            }
        }

        service.openStackRESTService.get(NOVA, 'servers/detail').returns(map)
        service.networkService.isUseExternalFLIP().returns(false)
        service.networkService.getFloatingIps().returns([]).stub()
        play{
            assertArrayEquals(instances.toArray(), service.getAllActiveInstances().toArray())
        }
    }


    void testGetById() {
        def externalFloatingIp = new ExternalFloatingIp()
        externalFloatingIp.fixedIpAddress='1.1.1.1'
        externalFloatingIp.floatingIpAddress='8.8.8.8'
        def externalFloatingIps = [externalFloatingIp]

        service.openStackRESTService.get(NOVA, "servers/$instance1AsMap.id").returns([server: instance1AsMap]).times(1)
        service.networkService.getFloatingIpsForInstance(instance1AsMap.id).returns(floatingIp).times(1)
        service.networkService.isUseExternalFLIP().returns(true).times(1)
        service.networkService.getExternalFloatingIps().returns(externalFloatingIps).times(1)
        service.quantumDNSService.getFqdnByIp('127.0.0.3').returns(FQDN).times(1)

        Instance instance = new Instance(instance1AsMap)
        instance.networks[0].fqdn = FQDN
        play{
            assertEquals(instance, service.getById(instance1AsMap.id, true, true))
        }
    }


    void testGetByIdWithOutFillingFloatingIPs() {
        service.openStackRESTService.get(NOVA, "servers/$instance1AsMap.id").returns([server: instance1AsMap]).times(1)
        Instance instance = new Instance(instance1AsMap)

        play{
            assertEquals(instance, service.getById(instance1AsMap.id, false))
        }
    }


    void testIsSendAZOnCreate() {
        SessionStorageService sessionStorageServiceMock = mock(SessionStorageService)
        sessionStorageServiceMock.isFlagEnabled('instance_create_send_availibility_zone').returns(true)
        service.sessionStorageService = sessionStorageServiceMock
        play{
            assertTrue(service.isSendAZOnCreate())
        }
    }


    void testCreation() {
        Instance instance = new Instance(instance1AsMap)

        SessionStorageService sessionStorageServiceMock = mock(SessionStorageService)
        sessionStorageServiceMock.isFlagEnabled('instance_create_send_availibility_zone').returns(true)
        service.sessionStorageService = sessionStorageServiceMock

        service.openStackRESTService.post(NOVA, 'servers', bodyForInstanceCreation).returns(instance)
        play{
            assertEquals(instance, service.create(instanceMapForCreation))
        }
    }


    void testCreation2() {
        Instance instance = new Instance(instance1AsMap)
        service.sessionStorageService = mock(SessionStorageService)
        service.sessionStorageService.isFlagEnabled('instance_create_send_availibility_zone').returns(true)
        service.openStackRESTService.post(NOVA, 'servers', bodyForInstanceCreation2).returns(instance)
        play{
            assertEquals(instance, service.create(instanceMapForCreation2))
        }
    }


    void testExists() {
        service.openStackRESTService.get(NOVA, 'servers/detail').returns(instanceListAsMap).times(2)

        play {
            assertTrue(service.exists(instance1AsMap.name))
            assertFalse(service.exists('123'))
        }
    }


    void testIsShowAdminLoginCredentials() {
        play {
            assertTrue(service.isShowAdminLoginCredentials())
        }
    }


    void testIsShowUserLoginCredentials() {
        play {
            assertFalse(service.isShowUserLoginCredentials())
        }
    }
}
