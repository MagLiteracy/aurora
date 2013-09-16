
from restbasetest import *
from common.rest.settings_helper import TenantHelper, UserHelper


class TestTenantRequests(RESTBaseTest):

    @classmethod
    def setup_class(cls):
        super(TestTenantRequests, cls).setup_class()
        cls.thelper = TenantHelper(cls.utils)
        cls.uhelper = UserHelper(cls.utils)

    def teardown(self):
        # after each test-case: remove tenant that was created
        self.utils.cleanup_objects(self.thelper.delete_tenant, 'tenants')
        self.utils.cleanup_objects(self.uhelper.delete_user, 'users')

    def test_list_of_tenants(self):
        tenants = self.utils.get_list('tenants')
        # if get_list returned value then it is JSON object (data validated inside of get_list).
        ok_(type(tenants) == list, "Unable to get list of tenants.")

    def test_create_delete_tenant(self):
        tenant = self.thelper.create_tenant()
        ok_(tenant is not False, "Attempt to create tenant failed.")
        # delete tenant
        self.thelper.delete_tenant(tenant['id'])
        remaining = [t for t in self.utils.get_list('tenants') if t['id'] == tenant['id']]
        ok_(len(remaining) == 0, "'Delete tenant' failed.")

    def test_show_tenant(self):
        # `shown` contains tenant details plus list of quotas and users.
        # shown.tenants dict should correspond to `created` dict.
        created = self.thelper.create_tenant()
        shown = self.thelper.show_tenant(created['id'])
        ok_(created == shown['tenant'], "'Show tenant' failed. Expected: %s, Actual: %s." % (created, shown))

    def test_update_tenant(self):
        created = self.thelper.create_tenant()

        new_name = created['name'] + '_updated'
        params = {
            'id': created['id'],
            'name': new_name,
            'description': created['description'],
            'enabled': 'on'  # optional
        }
        updated = self.thelper.update_tenant(params)
        ok_(updated['name'] == new_name, "'Update tenant' failed.")

    def test_list_of_tenant_quotas(self):
        tenant = self.utils.get_list('tenants')[0]
        tquotas = self.thelper.get_tenant_quotas(tenant['id'])
        ok_(tquotas is not False, "'List of tenant quotas' failed.")

    def test_update_tenant_quotas(self):
        # create tenant
        tenant = self.thelper.create_tenant()
        # update quotas
        params = {'id': tenant['id'], 'quota': {'key_pairs': 55, 'security_groups': 55}}
        self.thelper.update_tenant_quotas(params)
        # verify quotas were updated
        newquotas = self.thelper.get_tenant_quotas(tenant['id'])
        for q in newquotas:
            if q['name'] in ('key_pairs', 'security_groups'):
                ok_(q['limit'] == '55', "'Update tenant quotas' failed.")

    def test_list_of_tenant_policies(self):
        tenant = self.utils.get_list('tenants')[0]
        tpolicies = self.thelper.get_tenant_policies(tenant['name'])
        ok_(tpolicies is not False, "'List of tenant policies' failed.")

    def test_list_of_tenant_users(self):
        tenant = self.utils.get_list('tenants')[0]
        tusers = self.thelper.get_tenant_users(tenant['id'])
        ok_(tusers is not False, "'List of tenant users' failed.")

    def test_update_tenant_users(self):
        # create tenant
        tenant = self.thelper.create_tenant()

        # select initial and resulting roles
        roles = self.thelper.get_all_roles(tenant)
        init_role = roles[0]
        res_role = roles[1]

        # create new user with init_role for tenant
        uname = self.utils.generate_string(4, *[u['name'] for u in self.thelper.get_all_users(tenant)])
        params = {
            'name': uname,
            'tenant_id': tenant['id'],
            'role_id': init_role['id']
        }
        new_user = self.uhelper.create_user(params)

        # build parameters for update_user request
        uid2name = {user['id']: user['name'] for user in self.thelper.get_all_users(tenant)}  # map user ids to names
        init_users_roles = self.thelper.get_tenant_users(tenant['id'])
        new_users_roles = {uid2name[uid]: role['id'] for uid, role in init_users_roles.iteritems()}
        new_users_roles[uname] = res_role['id']  # role update
        params = {
            'tenantId': tenant['id'],
            'newUsersRoles': json.dumps(new_users_roles),
        }
        # update tenant's user and verify result
        self.thelper.update_tenant_users(params)
        res_users_roles = self.thelper.get_tenant_users(tenant['id'])
        updated_role = res_users_roles[new_user['id']]
        ok_(updated_role['id'] == res_role['id'], "'Update tenant users' failed.")

if __name__ == '__main__':
    t = TestTenantRequests()
    t.setup_class()
    # t.test_create_delete_tenant()
    # t.test_update_tenant_users()
    # t.test_update_tenant_quotas()
    t.test_update_tenant()
    t.teardown()

