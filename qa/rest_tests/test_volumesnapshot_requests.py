from restbasetest import *
from common.rest.storage_helper import VolumeHelper, VolumeSnapshotHelper


class TestVolumeSnapshotRequests(RESTBaseTest):

    @classmethod
    def setup_class(cls):
        super(TestVolumeSnapshotRequests, cls).setup_class()

        cls.vhelper = VolumeHelper(cls.utils)
        cls.shelper = VolumeSnapshotHelper(cls.utils)

        # remove snapshots and volumes to relieve storage
        cls.utils.cleanup_objects(cls.shelper.delete_snapshot, 'snapshots')
        cls.utils.cleanup_objects(cls.vhelper.delete_volume, 'volumes', name_key='displayName')

    def teardown(self):
        self.utils.cleanup_objects(self.shelper.delete_snapshot, 'snapshots')
        self.utils.cleanup_objects(self.vhelper.delete_volume, 'volumes', name_key='displayName')

    def test_list_of_snapshots(self):
        snapshots = self.utils.get_list('snapshots')
        ok_(type(snapshots) == list, "Unable to get list of snapshots.")

    def test_create_show_delete_snapshot(self):
        # operations with volumes take a lot of storage and time, so were implemented as one sequence.

        # create volume
        vol = self.vhelper.create_volume()
        # create volume snapshot
        created_snap = self.shelper.create_snapshot(parameters={'id': vol['id']})
        # verify snapshot for correct volume was created
        ok_(created_snap['volumeId'] == vol['id'], "Unable to create volume snapshot.")

        # show snapshot
        shown_snap = self.shelper.show_snapshot(created_snap['id'])
        ok_(created_snap == shown_snap,
            "'Show snapshot' failed. Expected: %s, Actual: %s." % (created_snap, shown_snap))

        # delete snapshot
        res = self.shelper.delete_snapshot(created_snap['id'])
        ok_(res is True, "Unable to delete volume snapshot.")

# just for local debugging
if __name__ == "__main__":
    t = TestVolumeSnapshotRequests()
    t.setup_class()
    t.test_list_of_snapshots()
    t.test_create_show_delete_snapshot()
    t.teardown()