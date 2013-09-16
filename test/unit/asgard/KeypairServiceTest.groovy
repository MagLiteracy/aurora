package asgard

import com.paypal.asgard.KeypairService
import com.paypal.asgard.OpenStackRESTService
import com.paypal.asgard.model.Keypair
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(KeypairService)
class KeypairServiceTest extends GMockTestCase {

    static final NOVA = "compute"
    static final keypair1 = [name: 'name1', public_key: 'public_key1', fingerprint: 'fingerprint1']
    static final keypair2 = [name: 'name2', public_key: 'public_key2', fingerprint: 'fingerprint2']
    static final non_existing_keypair = [name: 'name3', public_key: 'public_key3', fingerprint: 'fingerprint3']
    static final keypairs = [keypairs: [[keypair: keypair1],[keypair:keypair2]]]


    @Before
    void setUp() {
        OpenStackRESTService openStackRESTService = mock(OpenStackRESTService);
        service.openStackRESTService = openStackRESTService
        service.openStackRESTService.NOVA.returns(NOVA).stub()

        service.openStackRESTService.get(NOVA, 'os-keypairs').returns(keypairs).stub()

    }

    def testGetAllNames() {
        play {
            assertEquals(['name1', 'name2'], service.getAllNames())
        }
    }

    def testListAll() {
        play {
            assertEquals([new Keypair(keypair1), new Keypair(keypair2)], service.listAll())
        }
    }

    def testExists() {
        play {
            assertTrue(service.exists('name1'))
            assertFalse(service.exists('wrong_name'))
        }
    }

    def testGetKeypairByName() {
        play {
            assertNull(service.getKeypairByName('wrong_name'))
            assertEquals(new Keypair(keypair1), service.getKeypairByName(keypair1.name) )
        }
    }

    def testCreate() {
        service.openStackRESTService.post(NOVA, 'os-keypairs', [keypair: [name: 'name3']]).returns([keypair: non_existing_keypair]).stub()
        service.openStackRESTService.post(NOVA, 'os-keypairs', [keypair: [name: 'name1']]).returns([keypair: keypair1]).stub()
        play {
            assertNull(service.create(keypair1.name))
            assertEquals(new Keypair(non_existing_keypair), service.create(non_existing_keypair.name))
        }
    }

    def testInsert() {
        service.openStackRESTService.post(NOVA, 'os-keypairs', [keypair: [name: non_existing_keypair.name, public_key: non_existing_keypair.public_key]]).returns([keypair: non_existing_keypair]).stub()
        service.openStackRESTService.post(NOVA, 'os-keypairs', [keypair: [name: keypair1.name, public_key: keypair1.public_key]]).returns([keypair: keypair1]).stub()
        play {
            assertNull(service.insert(keypair1.name, keypair1.public_key))
            assertEquals(new Keypair(non_existing_keypair), service.insert(non_existing_keypair.name, non_existing_keypair.public_key))
        }
    }

    def testDelete() {
        service.openStackRESTService.delete(NOVA, "os-keypairs/name1").returns(null)
        play {
            assertNull(service.delete('name1'))
        }
    }
}
