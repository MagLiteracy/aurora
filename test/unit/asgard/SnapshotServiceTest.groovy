package asgard

import com.paypal.asgard.OpenStackRESTService
import com.paypal.asgard.SnapshotService
import com.paypal.asgard.model.Snapshot
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(SnapshotService)
class SnapshotServiceTest extends GMockTestCase {

    final static SNAPSHOTS = 'snapshots'
    final static NOVA_VOLUME = "volume"

    final static snapshot1 = [id: 'id1', display_name: 'name1', created_at: 'date1', display_description: 'desc1', size: 1, status: 'good', volume_id: 'volId1']
    final static snapshot2 = [id: 'id2', display_name: 'name2', created_at: 'date2', display_description: 'desc2', size: 1, status: 'good', volume_id: 'volId2']

    @Before
    void setUp() {
        OpenStackRESTService openStackRESTService = mock(OpenStackRESTService);
        service.openStackRESTService = openStackRESTService
        service.openStackRESTService.NOVA_VOLUME.returns(NOVA_VOLUME).stub()

    }

    def testGetAllSnapshots() {
        service.openStackRESTService.get(NOVA_VOLUME, "snapshots/detail").returns([snapshots: [snapshot1, snapshot2]]).stub()

        play {
            assertEquals([new Snapshot(snapshot1), new Snapshot(snapshot2)], service.getAllSnapshots())
        }

    }

    def testCreateSnapshot() {
        def body = [snapshot: [display_name: snapshot1.display_name, force: false, display_description: snapshot1.display_description, volume_id: snapshot1.volume_id]];
        service.openStackRESTService.post(NOVA_VOLUME, SNAPSHOTS, body).returns([snapshot: snapshot1]).stub()

        play {
            assertEquals([snapshot:snapshot1], service.createSnapshot([name: snapshot1.display_name, description: snapshot1.display_description , id: snapshot1.volume_id]))
        }

    }

    def testGetSnapshotById() {
        service.openStackRESTService.get(NOVA_VOLUME, "${SNAPSHOTS}/$snapshot1.id").returns([snapshot: snapshot1]).stub()
        play {
            assertEquals(new Snapshot(snapshot1), service.getSnapshotById(snapshot1.id))
        }
    }

    def testDeleteSnapshotById() {
        service.openStackRESTService.delete(NOVA_VOLUME, "${SNAPSHOTS}/$snapshot1.id").returns(null).stub()
        play {
            assertNull(service.deleteSnapshotById(snapshot1.id))
        }
    }

}
