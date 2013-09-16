from uibasetest import *


class TestSLbaasJobs(UIBaseTest):

    policies = []

    def setup(self):
        super(TestSLbaasJobs, self).setup()
        self.uidriver.click_menu(self.uimap.menu_lbaas, self.uimap.menu_jobs)

    def test_01_list_quotas(self):
        rows = self.uidriver.parse_table(self.uimap.tbl_lbaas_jobs, limit=1)
        ok_(len(rows) > 0, "There isn't any row in table. Interrupting.")
        creation_date = rows[0]["CREATION DATE"]
        self.uidriver.click(*self.uimap.bt_init_filter)
        self.uidriver.filter_table(creation_date)

        rows = self.uidriver.parse_table(self.uimap.tbl_lbaas_jobs)
        ok_(len(rows) == 1 and rows[0]["CREATION DATE"] == creation_date, "Failed to filter list of jobs.")
        self.uidriver.filter_table('')
