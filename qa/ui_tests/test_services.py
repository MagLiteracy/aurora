from uibasetest import *


class TestServices(UIBaseTest):

    policies = []

    def setup(self):
        super(TestServices, self).setup()
        self.uidriver.click_menu(self.uimap.menu_settings, self.uimap.menu_services)

    def test_01_list_services(self):
        rows = self.uidriver.list_table_by_rows(self.uimap.tbl_services, limit=1)
        ok_(len(rows) > 0, "There isn't any row in table. Interrupting.")
        name = rows[0][self.uidriver.get_tbl_headers(self.uimap.tbl_services).index("NAME")]
        self.uidriver.filter_table(name)

        cnt_flt = int(self.uidriver.find_element(*self.uimap.counter).text)
        cnt_tbl = self.uidriver.count_rows_with_filter(self.uimap.tbl_services, name)
        ok_(cnt_flt == cnt_tbl, "Failed to filter list of services.")
        self.uidriver.filter_table('')
