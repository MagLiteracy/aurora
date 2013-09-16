package asgard

import com.paypal.asgard.OpenStackRESTService
import com.paypal.asgard.SessionStorageService
import com.paypal.asgard.TenantService
import com.paypal.asgard.model.Tenant
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(TenantService)
class TenantServiceTest extends GMockTestCase {

    static final String KEYSTONE = 'identity'

    static final tenant1 = [id: 'id1', name: 'name1', description: 'desc1', enabled: true]
    static final tenant2 = [id: 'id2', name: 'name2', description: 'desc2', enabled: true]
    static final zonesAsString = 'zone1\nzone2\nzone3'
    static final zones = zonesAsString.split('\n')

    @Before
    void setUp() {
        service.openStackRESTService = mock(OpenStackRESTService)
        service.openStackRESTService.KEYSTONE.returns(KEYSTONE).stub()
        service.openStackRESTService.get(KEYSTONE, "tenants").returns([tenants: [tenant1, tenant2]]).stub()
        service.sessionStorageService = mock(SessionStorageService)
    }

    def testGetAllTenants() {
        play {
            assertEquals([new Tenant(tenant1), new Tenant(tenant2)], service.getAllTenants())
        }

    }

    def testGetTenantsMap() {
        play {
            def expected = [:]
            expected[tenant1.id] = new Tenant(tenant1)
            expected[tenant2.id] = new Tenant(tenant2)

            assertEquals(expected, service.getTenantsMap())
        }
    }

    def testCreateTenant() {
        def body = ["tenant": ["name": tenant1.name, "description": tenant1.description, "enabled": tenant1.enabled, zones: zones]]
        def bodyWithOutZones = ["tenant": ["name": tenant2.name, "description": tenant2.description, "enabled": tenant2.enabled]]
        service.openStackRESTService.post(KEYSTONE, "tenants", body).returns([tenant: tenant1]).stub()
        service.openStackRESTService.post(KEYSTONE, "tenants", bodyWithOutZones).returns([tenant: tenant2]).stub()

        play {
            assertEquals(new Tenant(tenant1), service.createTenant([name: tenant1.name, description: tenant1.description, enabled: 'on', zones: zonesAsString]))
            assertEquals(new Tenant(tenant2), service.createTenant([name: tenant2.name, description: tenant2.description, enabled: 'on']))
        }

    }

    def testUpdateTenant() {
        def body = ["tenant": tenant1]
        body.tenant.zones = zones
        def bodyWithOutZones = ["tenant": tenant2]
        service.openStackRESTService.post(KEYSTONE, "tenants/$tenant1.id", body).returns([tenant: tenant1]).stub()
        service.openStackRESTService.post(KEYSTONE, "tenants/$tenant2.id", bodyWithOutZones).returns([tenant: tenant2]).stub()

        play {
            assertEquals(new Tenant(tenant1), service.updateTenant([id: tenant1.id, name: tenant1.name, description: tenant1.description, enabled: 'on', zones: zonesAsString]))
            assertEquals(new Tenant(tenant2), service.updateTenant([id: tenant2.id, name: tenant2.name, description: tenant2.description, enabled: 'on']))
        }
    }

    def testGetTenantById() {
        service.openStackRESTService.get(KEYSTONE, "tenants/$tenant1.id").returns([tenant: tenant1]).stub()
        play {
            assertEquals(new Tenant(tenant1), service.getTenantById(tenant1.id))
        }
    }

    def testGetTenantByName() {
        play {
            assertEquals(new Tenant(tenant2), service.getTenantByName(tenant2.name))
        }
    }

    def testDeleteTenantById() {
        service.openStackRESTService.delete(KEYSTONE, "tenants/tenantId1").returns(null).stub()
        play {
            assertNull(service.deleteTenantById('tenantId1'))
        }
    }

    def testIsKeystoneCustomTenancy() {
        service.sessionStorageService.isFlagEnabled(TenantService.KEYSTONE_CUSTOM_TENANCY).returns(true).times(1)

        play {
            assertTrue(service.isKeystoneCustomTenancy())
        }
    }
}
