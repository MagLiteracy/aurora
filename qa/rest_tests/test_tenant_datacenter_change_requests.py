from restbasetest import *


class TestTenantDatacenterChangeRequests(RESTBaseTest):

    def test_list_of_datacenters(self):
        dcs = self.auth.get_datacenters()
        ok_(type(dcs) == list, "Unable to get list of datacenters.")

    def test_get_change_userstate(self):
        """ Test-case verifies the following requests: Change tenant, Change datacenter, Get current user state. """
        # get current user state
        old_dc, old_tenant = self.auth.get_user_state()
        ok_(old_dc and old_tenant, "'Get current user state' failed.")
        # select new values (if there are no other values, use the same)
        different_dcs = [dc for dc in self.auth.get_datacenters() if dc != old_dc]
        new_dc = old_dc if len(different_dcs) == 0 else different_dcs[0]
        diff_tenants = [t['id'] for t in self.utils.get_list('tenants') if t['id'] != old_tenant]
        new_tenant = old_tenant if len(diff_tenants) == 0 else diff_tenants[0]

        # change DC and tenant
        self.auth.change_datacenter(new_dc)
        self.auth.change_tenant(new_tenant)
        # get current user state and verify DC and tenant have been updated
        act_dc, act_tenant = self.auth.get_user_state()
        ok_(act_dc == new_dc, "'Change datacenter' failed.")
        ok_(act_tenant == new_tenant, "'Change tenant' failed.")
        # revert changes
        self.auth.change_datacenter(old_dc)
        self.auth.change_tenant(old_tenant)

if __name__ == '__main__':
    t = TestTenantDatacenterChangeRequests()
    t.setup_class()
    t.test_get_change_userstate()
    t.test_list_of_datacenters()