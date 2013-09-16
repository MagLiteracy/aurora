from restbasetest import *
from common.rest.settings_helper import UserHelper, TenantHelper


class TestUserRequests(RESTBaseTest):

    @classmethod
    def setup_class(cls):
        super(TestUserRequests, cls).setup_class()
        cls.uhelper = UserHelper(cls.utils)
        cls.thelper = TenantHelper(cls.utils)

    def teardown(self):
        # after each test-case: remove user that was created
        self.utils.cleanup_objects(self.uhelper.delete_user, 'users')

    def test_list_of_users(self):
        users = self.utils.get_list('users')
        ok_(type(users) is list, "Unable to get list of users.")

    def test_create_delete_user(self):
        users = self.utils.get_list('users')
        uname = self.utils.generate_string(4, *[u['name'] for u in users])

        params = {
            'name': uname,
            'email': uname + '@aurora.com'
        }
        self.uhelper.create_user(params)
        new_user = [u for u in self.utils.get_list('users') if u['name'] == uname]
        ok_(len(new_user) == 1, "Attempt to create user failed.")

        # delete user
        self.uhelper.delete_user([new_user[0]['id']])
        remaining = [u for u in self.utils.get_list('users')
                     if u['id'] == new_user[0]['id']]
        ok_(len(remaining) == 0, "'Delete user' failed.")

    def test_show_user(self):
        created = self.uhelper.create_user()
        shown = self.uhelper.show_user(created['id'])
        ok_(created == shown, "'Show user' failed. Expected: %s, Actual: %s." % (created, shown))

    def test_update_user(self):
        created = self.uhelper.create_user()

        new_name = created['name'] + '_updated'
        params = {
            'id': created['id'],
            'name': new_name,
            'email': new_name + '@rest.com',
            'password': '123456',
            'confirm_password': '123456',
            'tenant_id': created['tenantId']
        }

        updated = self.uhelper.update_user(params)['user']
        ok_(updated['name'] == new_name and updated['id'] == created['id'], "'Update user' failed.")

if __name__ == '__main__':
    t = TestUserRequests()
    t.setup_class()
    t.test_update_user()
    t.teardown()