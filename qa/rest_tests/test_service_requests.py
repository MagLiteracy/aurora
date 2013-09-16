from restbasetest import *


class TestServiceRequests(RESTBaseTest):

    def test_list_of_services(self):
        services = self.utils.get_list('services')
        ok_(type(services) == list, "Unable to get list of services.")
