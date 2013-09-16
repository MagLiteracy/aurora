package asgard

import com.paypal.asgard.*
import com.paypal.asgard.model.*
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(LbaasService)
class LbaasServiceTest extends GMockTestCase {

    static final LBMS = 'lbms'

    static final tenantName = 'tenantName1'
    static final tenantPath = "tenant/$tenantName/"

    static final jobId = 2404
    static final jobResponse = [Lb_Job_List: [jobIds: [jobId]]]

    static final poolPath = "${tenantPath}pools/"
    static final poolPathEditService = "${tenantPath}pools"

    static final poolName = 'poolName1'
    static final poolNamePath = "${poolPath}${poolName}"
    static final lbService = [enabled: 'true', ip: '127.0.0.1', name: 'serviceName1', port: 81, weight: 10]
    static final pool = [enabled: true, name: poolName, method: 'peeping', monitors: ['health'], services: [lbService]]
    static final poolServicesPath = "${tenantPath}pools/${poolName}/services"
    static final lbServicePath = "${poolServicesPath}/${lbService.name}"

    static final poolName2 = 'poolName2'
    static final pool2NamePath = "${poolPath}${poolName2}"
    static final lbService2 = [enabled: 'true', ip: '127.0.0.2', name: 'serviceName2', port: 82, weight: 10]
    static final pool2 = [enabled: true, name: poolName2, method: 'peeping', monitors: ['thirst', 'hunger'], services: [lbService2]]
    static final pool2ServicesPath = "${tenantPath}pools/${poolName2}/services"

    static final vipName = 'vipName1'
    static final vipPath = "${tenantPath}vips/"
    static final vipNamePath = "${vipPath}${vipName}"
    static final vip = [port: 80, enabled: true, name: vipName, protocol: 'HTTP', ip: '127.0.0.1']
    static final vipParams = [port: 80, enabled: 'on', name: vipName, protocol: 'HTTP', ip: '127.0.0.1']

    static final policyName = 'policyName1'
    static final policyPath = "${tenantPath}policies/"
    static final policyNamePath = "${policyPath}${policyName}"
    static final policy = [name: policyName, rule: 'rule1']

    static final jobPath = "${tenantPath}jobs"
    static final jobIdPath = "${jobPath}/${jobId}"
    static final job = [comments: 'comments1', completionDate: 'after', creationDate: 'before', jobId: "${jobId}",
            requestMethod: 'PUT', requestBody: "body", requestURI: '/uri', status: 'COMPLETE', tenantName: tenantName, taskType: 't']

    static final methodsPath = "${tenantPath}methods/"
    final methods = ['peeping']

    static final monitorsPath = "${tenantPath}monitors/"
    final monitors = ['health', 'thirst', 'hunger']



    @Before
    void setUp() {
        OpenStackRESTService openStackRESTService = mock(OpenStackRESTService);
        service.openStackRESTService = openStackRESTService
        service.openStackRESTService.LBMS.returns(LBMS).stub()

        SessionStorageService sessionStorageService = mock(SessionStorageService)
        service.sessionStorageService = sessionStorageService
        service.sessionStorageService.tenant.returns([name: tenantName]).stub()

        InstanceService instanceService = mock(InstanceService)
        service.instanceService = instanceService

        NetworkService networkService = mock(NetworkService)
        service.networkService = networkService
    }

    def testGetAllPools() {
        service.openStackRESTService.get(LBMS, poolNamePath).returns([pool: pool]).stub()

        service.openStackRESTService.get(LBMS, poolPath).returns(
                [tenantpools: [pools: [poolName, pool2], tenantName: tenantName]]
        )

        play {
            assertEquals([new Pool(pool), new Pool(pool2)], service.getAllPools())
        }

    }

    def testGetPool() {

        service.openStackRESTService.get(LBMS, poolNamePath).returns([pool: pool]).stub()

        play {
            assertEquals(new Pool(pool), service.getPool(poolName))
        }

    }

    def testDeletePool() {
        service.openStackRESTService.delete(LBMS, poolNamePath).returns(jobResponse).stub()

        play {
            assertEquals(jobResponse, service.deletePool(poolName))
        }
    }

    void testCreatePool() {
        def poolToCreate = [name: pool.name, method: pool.method, enabled: pool.enabled.toString(), monitors: pool.monitors]
        def poolParams = [name: pool.name, lbMethod: pool.method, enabled: pool.enabled, monitors: pool.monitors]

        service.openStackRESTService.put(LBMS, poolNamePath, null, [pool: poolToCreate]).returns(jobResponse).stub()

        play {
            assertNull(service.createPool(poolParams))
        }

    }

    void testUpdatePool() {
        def poolToUpdate = [name: pool.name, method: pool.method, enabled: pool.enabled.toString(), monitors: pool.monitors]
        def poolParams = [id: poolName2, name: pool.name, lbMethod: pool.method, enabled: pool.enabled, monitors: pool.monitors]

        service.openStackRESTService.post(LBMS, pool2NamePath, [pool: poolToUpdate]).returns(jobResponse).stub()

        play {
            assertEquals(service.updatePool(poolParams), jobResponse)
        }

    }

    def testGetVips() {
        service.openStackRESTService.get(LBMS, vipPath).returns([vip: [vip]]).stub()
        play {
            assertEquals([new Vip(vip)], service.getVips())
        }
    }

    def testGetVip() {
        service.openStackRESTService.get(LBMS, vipNamePath).returns([vip: vip]).stub()
        play {
            assertEquals(new Vip(vip), service.getVip(vipName))
        }
    }

    def testCreateVip() {
        service.openStackRESTService.put(LBMS, vipNamePath, null, [vip: vip]).returns(jobResponse).stub()

        play {
            assertEquals(jobResponse, service.createVip(vipParams))
        }
    }

    def testDeleteVip() {
        service.openStackRESTService.delete(LBMS, vipNamePath).returns(jobResponse).stub()

        play {
            assertEquals(jobResponse, service.deleteVip(vipName))
        }
    }

    def testGetPolicies() {
        service.openStackRESTService.get(LBMS, policyPath).returns([policy: [policy]]).stub()

        play {
            assertEquals([new Policy(policy)], service.getPolicies(tenantName))
        }
    }

    def testGetPolicy() {
        service.openStackRESTService.get(LBMS, policyNamePath).returns([policy: policy]).stub()

        play {
            assertEquals(new Policy(policy), service.getPolicy(policyName, tenantName))
        }
    }

    def testCreatePolicy() {
        service.openStackRESTService.put(LBMS, policyNamePath, null, [policy: policy]).returns(jobResponse).stub()

        play {
            assertEquals(jobResponse, service.createPolicy(policy, tenantName))
        }
    }

    def testUpdatePolicy() {
        service.openStackRESTService.post(LBMS, policyNamePath, [policy: policy]).returns(jobResponse).stub()

        def policyWithId = policy.clone()
        policyWithId['id'] = policyWithId.name

        play {
            assertEquals(jobResponse, service.updatePolicy(policyWithId, tenantName))
        }
    }

    def testDeletePolicy() {
        service.openStackRESTService.delete(LBMS, policyNamePath).returns(jobResponse).stub()

        play {
            assertEquals(jobResponse, service.deletePolicy(policyName, tenantName))
        }
    }

    def testGetJobs() {
        service.openStackRESTService.get(LBMS, jobPath).returns([Tenant_Job_Details: [job]]).stub()

        play {
            assertEquals([new Job(job)], service.getJobs())
        }
    }

    def testGetJobById() {
        service.openStackRESTService.get(LBMS, jobIdPath).returns([Tenant_Job_Details: job]).stub()

        play {
            assertEquals(new Job(job), service.getJobById(jobId))
        }
    }

    def testGetServices() {
        service.openStackRESTService.get(LBMS, poolServicesPath).returns([service: [lbService]]).stub()

        play {
            assertEquals([new LBService(lbService)], service.getServices(poolName))
        }
    }

    def testGetServicesByInstance() {
        service.openStackRESTService.get(LBMS, poolNamePath).returns([pool: pool]).stub()

        service.openStackRESTService.get(LBMS, poolPath).returns(
                [tenantpools: [pools: [poolName, pool2], tenantName: tenantName]]
        )

        Instance instance = new Instance()
        instance.networks << new IPContainer(lbService.ip, 'network_name')

        play {
            assertEquals([[pool: poolName, ip: lbService.ip, enabled: lbService.enabled, name: lbService.name, port: lbService.port]], service.getServicesByInstance(instance))
        }

    }

    def testAddServicesWithExternalFLIP() {
        service.networkService.isUseExternalFLIP().returns(true).times(1)

        def instance = new Instance(InstanceServiceTest.instance1AsMap)
        instance.floatingIps << new IPContainer('127.0.0.3')

        service.instanceService.getById('instanceId1').returns(instance).times(1)

        def postedService = [name: lbService.name, ip: '127.0.0.3', port: '82', weight: '12', enabled: 'true']

        service.openStackRESTService.post(LBMS, poolPathEditService, [pool: [[name: poolName, services: [postedService]]]]).returns([]).times(1)

        play {
            service.addServices(['instanceId1'], poolName, lbService.name, 'eth0', '82', '12', true)
        }

    }

    def testAddServicesWithOutExternalFLIP() {
        service.networkService.isUseExternalFLIP().returns(false).times(1)

        def instance = new Instance(InstanceServiceTest.instance1AsMap)
        instance.floatingIps << new IPContainer('127.0.0.3')
        IPContainer flip = new IPContainer('127.0.0.4')
        flip.pool = 'eth0'
        instance.floatingIps << flip

        service.instanceService.getById('instanceId1').returns(instance).times(1)

        def postedService1 = [name: lbService.name, ip: '127.0.0.3', port: '82', weight: '12', enabled: 'true']
        def postedService2 = [name: lbService.name, ip: '127.0.0.4', port: '82', weight: '12', enabled: 'true']

        service.openStackRESTService.post(LBMS, poolPathEditService, [pool: [[name: poolName, services: [postedService1, postedService2]]]]).returns([]).times(1)

        play {
            service.addServices(['instanceId1'], poolName, lbService.name, 'eth0', '82', '12', true)
        }

    }

    def testDeleteServices() {
        service.openStackRESTService.get(LBMS, poolServicesPath).returns([service: [lbService]]).stub()
        service.openStackRESTService.get(LBMS, pool2ServicesPath).returns([service: [lbService2]]).stub()

        service.openStackRESTService.get(LBMS, poolNamePath).returns([pool: pool]).stub()
        service.openStackRESTService.get(LBMS, pool2NamePath).returns([pool: pool2]).stub()

        service.openStackRESTService.get(LBMS, poolPath).returns(
                [tenantpools: [pools: [poolName, pool2], tenantName: tenantName]]
        )

        service.openStackRESTService.delete(LBMS, lbServicePath).returns(jobResponse).stub()

        play {
            assertEquals([jobResponse], service.deleteServices([lbService.ip]))
        }

    }

    def testDeleteService() {

        service.openStackRESTService.delete(LBMS, lbServicePath).returns(jobResponse).stub()

        play {
            assertEquals(jobResponse, service.deleteService(poolName, lbService.name))
        }
    }

    def testChangeEnabled() {
        service.openStackRESTService.get(LBMS, poolServicesPath).returns([service: [lbService]]).times(1)

        service.openStackRESTService.post(LBMS, poolPathEditService, [pool: [[name: poolName, services: [lbService]]]]).returns([]).times(1)

        play {
            service.changeEnabled(poolName, [lbService.name], true)
        }

    }

    def testGetAllServices() {
        service.openStackRESTService.get(LBMS, poolNamePath).returns([pool: pool]).stub()

        service.openStackRESTService.get(LBMS, poolPath).returns(
                [tenantpools: [pools: [poolName, pool2], tenantName: tenantName]]
        )

        play {
            assertEquals([
                    [pool: poolName, ip: lbService.ip, enabled: lbService.enabled, name: lbService.name, port: lbService.port],
                    [pool: poolName2, ip: lbService2.ip, enabled: lbService2.enabled, name: lbService2.name, port: lbService2.port]
            ], service.getAllServices())
        }
    }

    def testGetMethods() {
        service.openStackRESTService.get(LBMS, methodsPath).returns([methods: methods]).stub()

        play {
            assertEquals(methods, service.getMethods())
        }
    }

    def testGetMonitors() {
        service.openStackRESTService.get(LBMS, monitorsPath).returns([monitors: monitors]).stub()

        play {
            assertEquals(monitors, service.getMonitors())
        }
    }

    def testIsServiceEnabled() {
        service.openStackRESTService.isServiceEnabled(LBMS).returns(true).times(1)

        play {
            assertTrue(service.isEnabled())
        }
    }
}
