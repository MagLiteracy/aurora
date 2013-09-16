from restbasetest import *
from common.rest.networking_helper import NetworkingHelper
from common.rest.compute_helper import InstanceHelper


class TestNetworkingRequests(RESTBaseTest):

    @classmethod
    def setup_class(cls):
        super(TestNetworkingRequests, cls).setup_class()

        cls.net_helper = NetworkingHelper(cls.utils)
        cls.ihelper = InstanceHelper(cls.utils, cls.auth)

        # remove objects to relieve storage
        cls.utils.cleanup_objects(cls.ihelper.terminate_instances, 'instances', id_key='instanceId')

    def teardown(self):
        # delete objects created by test-case
        self.utils.cleanup_objects(self.shelper.delete_network, 'networkId')
        self.utils.cleanup_objects(self.ihelper.terminate_instances, 'instances', id_key='instanceId')

    def test_list_of_networks(self):
        networks = self.utils.get_list('networks')
        ok_(type(networks) == list, "Unable to get list of volumes.")

    def test_create_network(self):
        network = self.vhelper.create_volume()
        # actually, these verifications are already done in create_volume()
        # but the test should contain the checks it was created for.
        ok_(volume is not False, "Attempt to create volume failed.")

    def test_show_network(self):
        created = self.net_helper.create_network()
        shown = self.net_helper.show_network(created['id'])
        ok_(created == shown, "'Show network' failed. Expected: %s, Actual: %s." % (created, shown))

    def test_delete_network(self):
        # create volume
        network = self.net_helper.create_network()
        # delete volume
        res = self.net_helper.delete_network(network['id'])
        ok_(res is True, "Unable to delete network.")

    # def test_attach_detach_volume(self):
    #     # create instance
    #     instance = self.ihelper.create_instance()
    #     iid = instance['instanceId']
    #     # create volume
    #     volume = self.net_helper.create_volume()
    #     vid = volume['id']
    #     device = '/dev/vd' + 'qa'  # '/dev/vd' part copy-pasted from Aurora UI.
    #     # attach and verify
    #     params = {
    #         'id': vid,
    #         'instanceId': iid,
    #         'device': device
    #     }
    #     res = self.net_helper.attach_volume(params)
    #     ok_(res is not False, "'Attach volume' failed.")
    #
    #     # detach and verify
    #     res = self.net_helper.detach_volume({'id': vid, 'instanceId': iid})
    #     ok_(res is not False, "'Detach volume' failed.")
    #
    # def test_create_volume_type(self):
    #     type = self.net_helper.create_volume_type()
    #     ok_(type is not False, "Attempt to create volume type failed.")
    #
    # def test_show_volume_type(self):
    #     created = self.net_helper.create_volume_type()
    #     # create_volume_type returns dict of type parameters with ID as int but not str.
    #     # but show_volume_type returns dict with ID as str. So, to verify result, str() is used.
    #     created['id'] = str(created['id'])
    #     shown = self.net_helper.show_volume_type(created['id'])
    #     ok_(created == shown, "'Show volume type' failed. Expected: %s, Actual: %s." % (created, shown))
    #
    # def test_delete_volume_type(self):
    #     volume_type = self.vhelper.create_volume_type()
    #     ok_(self.net_helper.delete_volume_type(volume_type['id']), "Unable to delete volume type.")

# just for local debugging
if __name__ == "__main__":
    t = TestNetworkingRequests()
    t.setup_class()
    t.test_show_network()
    t.teardown()
