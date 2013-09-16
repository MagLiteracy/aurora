from restbasetest import *
from common.rest.compute_helper import FlavorHelper


class TestFlavorRequests(RESTBaseTest):

    @classmethod
    def setup_class(cls):
        super(TestFlavorRequests, cls).setup_class()

        cls.fhelper = FlavorHelper(cls.utils)

    def test_list_of_flavors(self):
        flrs = self.utils.get_list('flavors')
        ok_(type(flrs) == list, "Unable to get list of flavors.")

    def test_create_delete_flavor(self):
        res = self.fhelper.create_flavor()
        # verify flavor created
        flrs = self.utils.get_list('flavors')
        created = [f for f in flrs if f['name'] == res['name']]

        ok_(len(created) == 1, 'Unable to create flavor.')

        self.fhelper.delete_flavor(created[0]['id'])
        # verify flavor deleted
        flrs = self.utils.get_list('flavors')
        remaining = [f for f in flrs if f['id'] == created[0]['id']]
        ok_(len(remaining) == 0, 'Unable to delete flavor. %s still exists.' % remaining)

if __name__ == '__main__':
    t = TestFlavorRequests()
    t.setup_class()
    t.test_list_of_flavors()
    t.test_create_delete_flavor()
    t.teardown_class()