from restbasetest import *


class TestQuotaRequests(RESTBaseTest):

    def test_list_of_quotas(self):
        quotas = self.utils.get_list('quotas')
        ok_(type(quotas) == list, "Unable to get list of quotas.")
