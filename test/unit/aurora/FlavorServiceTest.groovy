package aurora

import com.paypal.aurora.FlavorService
import com.paypal.aurora.OpenStackRESTService
import com.paypal.aurora.model.Flavor
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(FlavorService)
class FlavorServiceTest extends GMockTestCase{

    static final map = getDataForFlavor()
    static final NOVA = "compute"

    @Before
    void setUp() {
        service.openStackRESTService = mock(OpenStackRESTService)
        service.openStackRESTService.NOVA.returns(NOVA).stub()
    }

    def testGetById() {
        service.openStackRESTService.get(NOVA, "flavors/$map.id").returns([flavor: map])
        play {
            Flavor flavor = service.getById('flavorId1')
            assertEquals(new Flavor(map), flavor)
        }
    }

    def testListAll() {
        service.openStackRESTService.get(NOVA, "flavors/detail").returns([flavors: [map]])
        play {
            List<Flavor> flavors = service.listAll()
            assertEquals(1, flavors.size())
            assertEquals(new Flavor(map), flavors[0])
        }
    }

    def testCreate() {
        def body = [flavor: getDataForFlavor()]
        body.flavor.id = null
        service.openStackRESTService.post(NOVA, "flavors", body).returns([flavor: map])
        play {
            def resp = service.create(getDataForCreate())
            assertEquals(map, resp.flavor)
        }

    }

    def testDelete() {
        service.openStackRESTService.delete(NOVA, "flavors/$map.id").returns(null)
        play {
            assertNull(service.delete(map.id))
        }
    }

    static getDataForFlavor() {
        def map = [:]

        map['id'] = 'flavorId1'
        map['name'] = 'name'
        map['ram'] = 2048
        map['disk'] = 10
        map['OS-FLV-EXT-DATA:ephemeral'] = 1
        map['swap'] = 10
        map['vcpus'] = 1
        map['rxtx_factor'] = 2.0
        map['os-flavor-access:is_public'] = true

        map
    }

    static getDataForCreate() {
        [
            name: 'name',
            ram: 2048,
            disk: 10,
            ephemeral: 1,
            swap: 10,
            vcpus: 1,
            rxtxFactor: 2,
            isPublic: 'on',
            id: null
        ]
    }
}
