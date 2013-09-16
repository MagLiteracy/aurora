from restbasetest import *
from common.rest.security_helper import SecurityGroupHelper


class TestSecurityGroupsRequests(RESTBaseTest):

    @classmethod
    def setup_class(cls):
        super(TestSecurityGroupsRequests, cls).setup_class()

        cls.ghelper = SecurityGroupHelper(cls.utils)

    def teardown(self):
        # remove remaining security groups
        self.utils.cleanup_objects(self.ghelper.delete_security_group, 'securitygroups')

    def test_list_of_groups(self):
        groups = self.utils.get_list('securitygroups')
        ok_(type(groups) == list, "Unable to get list of security groups.")

    def test_create_group(self):
        # create sec. group and verify it appeared in the list.
        created = self.ghelper.create_security_group()
        listed = [g for g in self.utils.get_list('securitygroups') if g['id'] == created['id']]
        ok_(len(listed) == 1, "Attempt to create sec. group failed.")

    def test_delete_group(self):
        # create sec. group
        group = self.ghelper.create_security_group()
        # delete sec. group
        res = self.ghelper.delete_security_group(group['id'])
        ok_(res is True, "Sec. group was not deleted.")

    def test_show_group(self):
        created = self.ghelper.create_security_group()
        gid = created['id']

        shown = self.ghelper.show_security_group(gid)
        ok_(created == shown, "'Show sec. group' failed. Expected: %s, Actual: %s." % (created, shown))

    def test_add_delete_rule(self):
        # create sec. group
        group = self.ghelper.create_security_group()
        gid = group['id']

        # add rule
        rule = self.ghelper.add_rule(gid)
        rid = str(rule['id'])

        # show group created before
        group = self.ghelper.show_security_group(gid)
        # check if it contains just created rule
        ok_(rid in (r['id'] for r in group['rules']), "'Add rule to sec. group' failed.")

        # delete rule
        res = self.ghelper.delete_rule(rid)

        # show group created before
        group = self.ghelper.show_security_group(gid)
        ok_(rid not in (r['id'] for r in group['rules']), "'Delete rule from sec. group' failed.")

# just for local debugging
if __name__ == "__main__":
    t = TestSecurityGroupsRequests()
    t.setup_class()
    # t.test_list_of_groups()
    # t.test_create_group()
    # t.test_delete_group()
    t.test_show_group()
    t.test_add_delete_rule()
    t.teardown()
    t.teardown_class()

