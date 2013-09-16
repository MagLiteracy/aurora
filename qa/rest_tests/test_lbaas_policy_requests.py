from restbasetest import *
from common.rest.lbaas_helper import PolicyHelper


class TestPolicyRequests(RESTBaseTest):

    @classmethod
    def setup_class(cls):
        super(TestPolicyRequests, cls).setup_class()
        cls.phelper = PolicyHelper(cls.utils)

    def teardown(self):
        self.utils.cleanup_objects(self.phelper.delete_policy, 'policies', id_key='name')

    def test_list_of_policies(self):
        policies = self.utils.get_list('policies')
        ok_(type(policies) == list, "Failed to get list of Policies.")

    def test_create_delete_policy(self):
        pname = self.utils.generate_string(3)
        params = {'name': pname}
        self.phelper.create_policy(params)
        new_policies = [p for p in self.utils.get_list('policies') if p['name'] == pname]
        ok_(len(new_policies) == 1, 'Failed to create LBaaS Policy.')
        # delete and verify result
        ok_(self.phelper.delete_policy(pname),
            'Failed to delete LBaaS policy. Policy "%s" still exists.' % pname)

    def test_update_policy(self):
        pname = self.utils.generate_string(3)
        self.phelper.create_policy({'name': pname})
        # get just created policy properties
        policy = [p for p in self.utils.get_list('policies') if p['name'] == pname][0]
        params = {
            'id': pname,
            'name': pname + '_updated',
            'rule': policy['rule']
        }
        self.phelper.update_policy(params)
        updated_policy = [p for p in self.utils.get_list('policies')
                          if p['name'] == pname + '_updated']
        ok_(len(updated_policy) == 1, 'Failed to update LBaaS policy.')

if __name__ == '__main__':
    t = TestPolicyRequests()
    t.setup_class()
    # t.test_list_of_policies()
    t.test_create_delete_policy()
    # t.test_update_policy()
    t.teardown()
