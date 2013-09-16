from uibasetest import *


class TestFloatingIp(UIBaseTest):

    floating_ip = []

    def setup(self):
        super(TestFloatingIp, self).setup()
        self.uidriver.click_menu(self.uimap.menu_networking, self.uimap.menu_floating_ip)

    def test_01_allocate_new_ip(self):
        self.uidriver.click(*self.uimap.bt_allocate)
        pool = self.uidriver.get_cb_options(self.uimap.cb_select_pool)
        ok_(len(pool) > 0, "Error: List of Pools is empty!")
        self.uidriver.select_cb_option(self.uimap.cb_select_pool, pool[0])
        name = self.utils.generate_name(4)
        self.uidriver.enter_text(self.uimap.ed_hostname, name)
        zone = self.uidriver.get_cb_options(self.uimap.cb_select_zone)
        ok_(len(zone) > 0, "Error: List of Zones is empty!")
        self.uidriver.select_cb_option(self.uimap.cb_select_zone, zone[0])
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        m = self.uidriver.find_row(self.uimap.tbl_floating_ip, lambda r: r["IP"] == name)
        ok_(m, "Floating IP: %s not in list" % name)

        self.floating_ip.append(name)

    def test_02_list_floating_ip(self):
        ok_(len(self.floating_ip) > 0, "Floating IP creation failed so cannot filter the list.")
        name = self.floating_ip[0]
        self.uidriver.filter_table(name)
        cnt_flt = int(self.uidriver.find_element(*self.uimap.counter).text)
        cnt_tbl = self.uidriver.count_rows_with_filter(self.uimap.tbl_floating_ip, name)
        ok_(cnt_flt == cnt_tbl, "Failed to filter list of Floating IPs.")
        # rows = self.uidriver.parse_table(self.uimap.tbl_floating_ip)
        # ok_(len(rows) == 1 and rows[0]['NAME'] == name, "Failed to filter list of Floating IPs.")
        self.uidriver.filter_table('')

    def test_03_show_floating_ip(self):
        ok_(len(self.floating_ip) > 0, "Floating IP creation failed so so nothing can be shown.")
        name = self.floating_ip[0]
        self.uidriver.click(By.LINK_TEXT, name)
        res = self.uidriver.is_element_present(*self.uimap.tbl_floating_ip)
        ok_(res, "Failed to show Floating IP quotas.")
        details = self.uidriver.get_details(self.uimap.tbl_floating_ip)
        ok_(details["NAME"] == name, "Failed to show Floating IPs' NAME.")
        # ok_(details["ID"] != "", "Failed to show Router's ID.")
        # ok_(details["STATUS"] != "", "Failed to show Router's STATUS.")

    def test_04_update_floating_ip(self):
        ok_(len(self.floating_ip) > 0, "Floating IP creation failed so nothing can be updated.")
        name = self.floating_ip[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_edit)
        # pool = self.uidriver.get_cb_options(self.uimap.cb_select_pool)
        # self.uidriver.select_cb_option(self.uimap.cb_select_pool, pool[0])
        name = self.utils.generate_name(4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        # zone = self.uidriver.get_cb_options(self.uimap.cb_select_zone)
        # self.uidriver.select_cb_option(self.uimap.cb_select_zone, zone[0])
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        self.uidriver.click(*self.uimap.bt_up)

        isOK = False
        row = self.uidriver.find_row(self.uimap.tbl_floating_ip, lambda r: r['NETWORK NAME'] == name)
        if row:
            self.floating_ip[0] = name
            if row[0]['NETWORK NAME'] == name and row[0]['STATUS'] == "ACTIVE":
                isOK = True
        ok_(isOK, "Failed to update of Floating IP.")

    def test_07_delete_floating_ip(self):
        ok_(len(self.floating_ip) > 0, "Floating IP creation failed so cannot delete it.")
        name = self.floating_ip[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        row = self.uidriver.find_row(self.uimap.tbl_floating_ip, lambda r: r['NAME'] == name)
        ok_(len(row) == 0, "Floating IP: {0} not deleted".format(name))
