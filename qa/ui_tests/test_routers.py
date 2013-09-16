from uibasetest import *


class TestRouters(UIBaseTest):

    routers = []

    def setup(self):
        super(TestRouters, self).setup()
        res = self.uidriver.click_menu(self.uimap.menu_networking, self.uimap.menu_routers)
        ok_(res, "The menu item doesn't exist")

    def test_01_create_router(self):
        self.uidriver.click(*self.uimap.bt_create)
        name = self.utils.generate_name(4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        m = self.uidriver.find_row(self.uimap.tbl_routers, lambda r: r["NAME"] == name)
        ok_(m, "Router: %s not in list" % name)
        self.routers.append(name)

    def test_02_list_routers(self):
        ok_(len(self.routers) > 0, "Router creation failed so cannot filter the list.")
        name = self.routers[0]
        self.uidriver.filter_table(name)
        cnt_flt = int(self.uidriver.find_element(*self.uimap.counter).text)
        cnt_tbl = self.uidriver.count_rows_with_filter(self.uimap.tbl_routers, name)
        ok_(cnt_flt == cnt_tbl, "Failed to filter list of Routers.")
        #
        # rows = self.uidriver.parse_table(self.uimap.tbl_routers)
        # ok_(len(rows) == 1 and rows[0]['NAME'] == name, "Failed to filter list of Routers.")
        self.uidriver.filter_table('')

    def test_03_show_router(self):
        ok_(len(self.routers) > 0, "Router creation failed so nothing can be shown.")
        name = self.routers[0]
        self.uidriver.click(By.LINK_TEXT, name)
        res = self.uidriver.is_element_present(*self.uimap.tbl_show_router)
        ok_(res, "Failed to show Router details.")
        details = self.uidriver.get_details(self.uimap.tbl_show_router)
        ok_(details["NAME"] == name, "Failed to show Router's NAME.")
        ok_(details["ID"] != "", "Failed to show Router's ID.")
        ok_(details["STATUS"] != "", "Failed to show Router's STATUS.")

    def test_04_update_router(self):
        ok_(len(self.routers) > 0, "Router creation failed so nothing can be updated.")
        name = self.routers[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_set_gw)
        networks = self.uidriver.get_cb_options(self.uimap.cb_select_network)
        ok_(len(networks) > 0, "Error: List of networks is empty!")
        # 1) Select Project:
        self.uidriver.select_cb_option(self.uimap.cb_select_network, networks[0])
        router_name = self.uidriver.find_element(*self.uimap.ed_name).get_attribute('value')
        if not router_name:
            name = self.utils.generate_name(4)
            # 2) Enter Router name:
            self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        details = self.uidriver.get_details(self.uimap.tbl_show_router)
        ok_(details["NAME"] == name, "Failed to update of Router.")

        # isOK = False
        # row = self.uidriver.find_row(self.uimap.tbl_routers, lambda r: r['NAME'] == name)
        # if row:
        #     self.routers[0] = name
        #     if row[0]['NAME'] == name and row[0]['STATUS'] == "ACTIVE":
        #         isOK = True
        # ok_(isOK, "Failed to update of Router.")

    def test_07_delete_router(self):
        ok_(len(self.routers) > 0, "Router creation failed so cannot delete it.")
        name = self.routers[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        row = self.uidriver.find_row(self.uimap.tbl_routers, lambda r: r['NAME'] == name)
        ok_(len(row) == 0, "Router: {0} not deleted".format(name))
