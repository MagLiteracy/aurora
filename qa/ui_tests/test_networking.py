from uibasetest import *


class TestNetworking(UIBaseTest):

    networks = []

    def setup(self):
        super(TestNetworking, self).setup()
        self.uidriver.click_menu(self.uimap.menu_networking, self.uimap.menu_networks)

    def test_01_create_network(self):
        self.uidriver.click(*self.uimap.bt_create)
        name = self.utils.generate_name(4)
        # 1) Enter Network Name:
        self.uidriver.enter_text(self.uimap.ed_name, name)
        projects = self.uidriver.get_cb_options(self.uimap.cb_select_project)
        ok_(len(projects) > 0, "Error: List of Projects is empty!")
        proj = "openstack"
        project = proj if proj in projects else projects[0]
        # 2) Select Project:
        self.uidriver.select_cb_option(self.uimap.cb_select_project, project)
        # 3) Select check box Admin State:
        if not self.uidriver.find_element(*self.uimap.chk_admin_state).is_selected():
            self.uidriver.click(*self.uimap.chk_admin_state)
        # 4) Select check box Shared:
        if not self.uidriver.find_element(*self.uimap.chk_shared).is_selected():
            self.uidriver.click(*self.uimap.chk_shared)
        # 5) Select check box External Network:
        if not self.uidriver.find_element(*self.uimap.chk_external).is_selected():
            self.uidriver.click(*self.uimap.chk_external)
        # 5) Submit:
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        m = self.uidriver.find_row(self.uimap.tbl_networks, lambda r: r["NETWORK NAME"] == name)
        ok_(len(m), "Network: %s not in list" % name)

        self.networks.append(name)

    def test_02_list_networks(self):
        ok_(len(self.networks) > 0, "Tenant creation failed so cannot filter the list.")
        name = self.networks[0]
        self.uidriver.filter_table(name)
        rows = self.uidriver.parse_table(self.uimap.tbl_networks)
        ok_(len(rows) == 1 and rows[0]['NETWORK NAME'] == name, "Failed to filter list of Networks.")
        self.uidriver.filter_table('')

    def test_03_show_network(self):
        ok_(len(self.networks) > 0, "Tenant creation failed so cannot filter the list.")
        name = self.networks[0]
        self.uidriver.click(By.LINK_TEXT, name)
        res = self.uidriver.is_element_present(*self.uimap.tbl_show_network)
        ok_(res, "Failed to show Network details.")

    def test_04_update_network(self):
        ok_(len(self.networks) > 0, "Tenant creation failed so cannot filter the list.")
        name = self.networks[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_edit)
        name = self.utils.generate_name(4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        self.uidriver.click(*self.uimap.bt_up)

        isOK = False
        row = self.uidriver.find_row(self.uimap.tbl_networks, lambda r: r['NETWORK NAME'] == name)
        if row:
            self.networks[0] = name
            if row[0]['NETWORK NAME'] == name and row[0]['STATUS'] == "ACTIVE":
                isOK = True
        ok_(isOK, "Failed to update of Network.")

    def test_07_delete_network(self):
        ok_(len(self.networks) > 0, "Tenant creation failed so cannot delete it.")
        name = self.networks[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        row = self.uidriver.find_row(self.uimap.tbl_networks, lambda r: r['NETWORK NAME'] == name)
        ok_(len(row) == 0, "Network: {0} not deleted".format(name))
