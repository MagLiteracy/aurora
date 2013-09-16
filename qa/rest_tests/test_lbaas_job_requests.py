from restbasetest import *


class TestJobRequests(RESTBaseTest):

    def test_list_of_jobs(self):
        jobs = self.utils.get_list('jobs')
        ok_(type(jobs) == list, "Failed to get list of LBaaS jobs.")

if __name__ == '__main__':
    t = TestJobRequests()
    t.setup_class()
    t.test_list_of_jobs()