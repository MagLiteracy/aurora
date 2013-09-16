from uibasetest import *


class TestFlavors(UIBaseTest):

    flavors = []

    def setup(self):
        super(TestFlavors, self).setup()
        self.uidriver.click_menu(self.uimap.menu_compute, self.uimap.menu_flavors)

    def test_01_create_flavor(self):
        self.uidriver.click(*self.uimap.bt_create)
        name = self.utils.generate_name(size=4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.enter_text(self.uimap.ed_ram, "128")
        self.uidriver.enter_text(self.uimap.ed_disk, "1")
        self.uidriver.enter_text(self.uimap.ed_vcpus, "1")
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        ok_(self.uidriver.is_element_in_table(self.uimap.tbl_flavors, "NAME", name), "Failed to create flavor.")
        self.flavors.append(name)

    def test_02_list_flavors(self):
        ok_(len(self.flavors) > 0, "Flavor creation failed so cannot filter the list.")
        name = self.flavors[0]
        self.uidriver.filter_table(name)
        cnt_flt = int(self.uidriver.find_element(*self.uimap.counter).text)
        cnt_tbl = self.uidriver.count_rows_with_filter(self.uimap.tbl_flavors, name)
        ok_(cnt_flt == cnt_tbl, "Failed to filter list of services.")
        self.uidriver.filter_table('')

    def test_03_remove_flavor(self):
        ui_flavors = [f['name'] for f in self.utils.get_list('flavors') if f['name'].startswith(self.prefix)]
        for name in ui_flavors:
            self.uidriver.click_row_checkbox(self.uimap.tbl_flavors, lambda r: r['NAME'] == name)
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(not self.uidriver.is_element_in_table(self.uimap.tbl_flavors, "NAME", name), "Failed to delete flavor(s).")
        # res = self.uidriver.find_row(self.uimap.tbl_flavors, lambda r: r['NAME'].startswith(self.prefix))
        # ok_(len(res) == 0, "Failed to delete flavor(s).")