package asgard

import com.paypal.asgard.OpenStackRESTService
import com.paypal.asgard.InstanceService
import com.paypal.asgard.FlavorService
import com.paypal.asgard.QuotaService
import com.paypal.asgard.SessionStorageService
import com.paypal.asgard.model.Flavor
import com.paypal.asgard.model.Quota
import com.paypal.asgard.model.QuotaUsage
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(QuotaService)
class QuotaServiceTest extends GMockTestCase {

    static final flavorAsMap = [
    'id': 'flavorId1',
    'name': 'name',
    'ram': 2048,
    'disk': 10,
    'OS-FLV-EXT-DATA:ephemeral': 1,
    'swap': 10,
    'vcpus': 1,
    'rxtx_factor': 2.0,
    'os-flavor-access:is_public': true]

    static final OS_QUOTAS = 'os-quota-sets'
    static final NOVA = 'compute'
    static final FLAVOR = new Flavor(flavorAsMap)

    static final quotas = [volumes: '2', count: '1', gigabytes: '4', ram: '4', instances: '5', cores: '6']
    static final quotasWithId = [volumes: '2', count: '1', gigabytes: '4', ram: '4', instances: '5', cores: '6', id: 'id1']
    static final quota1 = new Quota([key: 'count', value: 1])
    static final quota2 = new Quota([key: QuotaService.VOLUMES, value: 2])
    static final quota3 = new Quota([key: QuotaService.GIGABYTES, value: 4])
    static final quota4 = new Quota([key: QuotaService.RAM, value: 4])
    static final quota5 = new Quota([key: QuotaService.INSTANCES, value: 5])
    static final quota6 = new Quota([key: QuotaService.CORES, value: 6])

    static final quotaList = [quota1, quota2, quota3, quota4, quota5, quota6]
    static final quotaSet = [quota1, quota2, quota3, quota4, quota5, quota6]
    static final flavorList = [FLAVOR]
    static final String TENANT_ID = 'tenantId1'
    static final QuotaUsage quotaUsage1 = new QuotaUsage('Cores', 6, 1)
    static final QuotaUsage quotaUsage2 = new QuotaUsage('Instances', 5, 2)
    static final QuotaUsage quotaUsage3 = new QuotaUsage('Ram', 4, 2048)
    static final QuotaUsage quotaUsage4 = new QuotaUsage(QuotaService.DISK, 8, 10)
    static final quotaUsageList = [quotaUsage1, quotaUsage2, quotaUsage3, quotaUsage4]

    @Before
    void setUp() {
        quotaList.sort { it.displayName as String }
        service.openStackRESTService = mock(OpenStackRESTService)
        service.openStackRESTService.NOVA.returns(NOVA).stub()
        service.openStackRESTService.get(NOVA, OS_QUOTAS + "/$TENANT_ID").returns([quota_set: quotasWithId]).stub()

        service.sessionStorageService = mock(SessionStorageService)
        service.sessionStorageService.tenant.returns([id: TENANT_ID]).stub()

        service.flavorService = mock(FlavorService)
        service.flavorService.listAll().returns(flavorList).stub()

        service.instanceService = mock(InstanceService)
        service.instanceService.listAll().returns(InstanceServiceTest.instances).stub()
    }

    def testGetQuotasByTenantId() {

        play {
            assertEquals(quotaList, service.getQuotasByTenantId(TENANT_ID))
        }
    }

    def testGetAllQuotas() {
        play {
            assertArrayEquals(quotaList.toArray(), service.getAllQuotas())
        }
    }

    def testSetQuotasByTenantId() {
        service.openStackRESTService.put(NOVA, OS_QUOTAS + "/$TENANT_ID",null,[quota_set:quotas]).returns([quota_set: quotas]).stub()
        def resp = [quota_set: quotas]

        play {
            assertEquals(resp, service.setQuotasByTenantId(quotaSet, TENANT_ID))
        }
    }

    def testGetQuotaByName() {
        def name = 'emptyName'
        Quota emptyQuota = new Quota()
        emptyQuota.name = name
        emptyQuota.limit = 0
        emptyQuota.addDisplayName();

        play {
            assertEquals(quota1, service.getQuotaByName(quota1.name))
            assertEquals(emptyQuota, service.getQuotaByName(name))
        }
    }

    def testGetQuotaUsage() {

        play {
            assertEquals(quotaUsageList, service.getQuotaUsage(TENANT_ID))
        }
    }



}
