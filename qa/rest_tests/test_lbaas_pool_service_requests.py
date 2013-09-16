from restbasetest import *
from common.rest.lbaas_helper import PoolHelper
from common.rest.compute_helper import InstanceHelper


class TestPoolServiceRequests(RESTBaseTest):

    @classmethod
    def setup_class(cls):
        super(TestPoolServiceRequests, cls).setup_class()
        cls.ihelper = InstanceHelper(cls.utils, cls.auth)
        cls.phelper = PoolHelper(cls.utils)

        # create instance - its network will be used to create pools and itself will be used to create services.
        cls.instance = cls.ihelper.create_instance()
        ok_(cls.instance, "Unable to create instance. Pools creation will fail in the following test-cases.")
        cls.net_interface = cls.instance['networks'].keys()[0]

    @classmethod
    def teardown_class(cls):
        cls.utils.cleanup_objects(cls.phelper.delete_pool, 'pools', id_key='name')
        cls.utils.cleanup_objects(cls.ihelper.terminate_instances, 'instances', id_key='instanceId')
        super(TestPoolServiceRequests, cls).teardown_class()

    def test_list_of_pools(self):
        pools = self.utils.get_list('pools')
        ok_(type(pools) == list, "Unable to get list of pools.")

    def test_list_of_methods(self):
        methods = self.utils.get_list('methods')
        ok_(type(methods) == list, "Unable to get list of methods.")

    def test_list_of_monitors(self):
        monitors = self.utils.get_list('monitors')
        ok_(type(monitors) == list, "Unable to get list of monitors.")

    def test_create_delete_pool(self):
        created = self.phelper.create_pool({'netInterface': self.net_interface})
        pname = created['name']
        new_pools = [p for p in self.utils.get_list('pools') if p['name'] == pname]
        ok_(len(new_pools) == 1, 'Unable to create LBaaS pool.')
        # delete and verify result
        ok_(self.phelper.delete_pool([pname]), 'Unable to delete LBaaS pool. Pool named "%s" still exists.' % pname)

    def test_show_pool(self):
        created = self.phelper.create_pool({'netInterface': self.net_interface})
        shown = self.phelper.show_pool(created['name'])
        ok_(shown['pool']['name'] == created['name'], 'Unable to show LBaaS pool.')

    def test_create_delete_service(self):
        ip = self.instance['networks'][self.net_interface]
        pool = self.phelper.create_pool({'netInterface': self.net_interface})
        # create service for the pool
        sparams = {
            'id': pool['name'],
            'instanceId': self.instance['instanceId'],
            'netInterface': self.net_interface
        }
        service = self.phelper.create_service(sparams)
        new_services = [s for s in self.phelper.show_pool(pool['name'])['services'] if s['ip'] == ip]
        ok_(len(new_services) == 1, 'Unable to add service to LBaaS pool.')

        # delete service
        del_params = {'pool': pool['name'], 'selectedServices': service['name']}
        ok_(self.phelper.delete_service(del_params), 'Unable to delete BLaaS pool service.')

    def test_enable_disable_service(self):
        pool = self.phelper.create_pool({'netInterface': self.net_interface})
        # create service for the pool
        sparams = {
            'id': pool['name'],
            'instanceId': self.instance['instanceId'],
            'netInterface': self.net_interface
        }
        service = self.phelper.create_service(sparams)

        # enable service
        enable_disable_params = {
            'pool': pool['name'],
            'selectedServices': service['name']
        }
        ok_(self.phelper.enable_service(enable_disable_params), '"Enable service" failed.')
        # disable service
        ok_(self.phelper.disable_service(enable_disable_params), '"Disable service" failed.')

if __name__ == '__main__':
    t = TestPoolServiceRequests()
    t.setup_class()
    # t.test_list_of_pools()
    # t.test_create_delete_pool()
    # t.test_show_pool()
    # t.test_create_delete_service()
    # t.test_enable_disable_service()
    t.teardown_class()