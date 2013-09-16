from restbasetest import *
from common.rest.storage_helper import VolumeHelper, VolumeSnapshotHelper
from common.rest.compute_helper import InstanceHelper


class TestVolumeRequests(RESTBaseTest):

    @classmethod
    def setup_class(cls):
        super(TestVolumeRequests, cls).setup_class()

        cls.vhelper = VolumeHelper(cls.utils)
        cls.ihelper = InstanceHelper(cls.utils, cls.auth)
        cls.shelper = VolumeSnapshotHelper(cls.utils)

        # remove objects to relieve storage
        cls.utils.cleanup_objects(cls.ihelper.terminate_instances, 'instances', id_key='instanceId')
        cls.utils.cleanup_objects(cls.shelper.delete_snapshot, 'snapshots')
        cls.utils.cleanup_objects(cls.vhelper.delete_volume, 'volumes', name_key='displayName')

    def teardown(self):
        # delete objects created by test-case
        self.utils.cleanup_objects(self.shelper.delete_snapshot, 'snapshots')
        self.utils.cleanup_objects(self.vhelper.delete_volume, 'volumes', name_key='displayName')
        self.utils.cleanup_objects(self.vhelper.delete_volume_type, 'volumetypes')
        self.utils.cleanup_objects(self.ihelper.terminate_instances, 'instances', id_key='instanceId')

    def test_list_of_volumes(self):
        vols = self.utils.get_list('volumes')
        ok_(type(vols) == list, "Unable to get list of volumes.")

    def test_create_volume(self):
        volume = self.vhelper.create_volume()
        # actually, these verifications are already done in create_volume()
        # but the test should contain the checks it was created for.
        ok_(volume is not False, "Attempt to create volume failed.")

    def test_show_volume(self):
        created = self.vhelper.create_volume()
        shown = self.vhelper.show_volume(created['id'])
        ok_(created == shown, "'Show volume' failed. Expected: %s, Actual: %s." % (created, shown))

    def test_delete_volume(self):
        # create volume
        volume = self.vhelper.create_volume()
        # delete volume
        res = self.vhelper.delete_volume(volume['id'])
        ok_(res is True, "Unable to delete volume.")

    def test_attach_detach_volume(self):
        # create instance
        instance = self.ihelper.create_instance()
        iid = instance['instanceId']
        # create volume
        volume = self.vhelper.create_volume()
        vid = volume['id']
        device = '/dev/vd' + 'qa'  # '/dev/vd' part copy-pasted from Aurora UI.
        # attach and verify
        params = {
            'id': vid,
            'instanceId': iid,
            'device': device
        }
        res = self.vhelper.attach_volume(params)
        ok_(res is not False, "'Attach volume' failed.")

        # detach and verify
        res = self.vhelper.detach_volume({'id': vid, 'instanceId': iid})
        ok_(res is not False, "'Detach volume' failed.")

    def test_create_volume_type(self):
        type = self.vhelper.create_volume_type()
        ok_(type is not False, "Attempt to create volume type failed.")

    def test_show_volume_type(self):
        created = self.vhelper.create_volume_type()
        # create_volume_type returns dict of type parameters with ID as int but not str.
        # but show_volume_type returns dict with ID as str. So, to verify result, str() is used.
        created['id'] = str(created['id'])
        shown = self.vhelper.show_volume_type(created['id'])
        ok_(created == shown, "'Show volume type' failed. Expected: %s, Actual: %s." % (created, shown))

    def test_delete_volume_type(self):
        volume_type = self.vhelper.create_volume_type()
        ok_(self.vhelper.delete_volume_type(volume_type['id']), "Unable to delete volume type.")

# just for local debugging
if __name__ == "__main__":
    t = TestVolumeRequests()
    t.setup_class()
    t.test_show_volume()
    t.teardown()
