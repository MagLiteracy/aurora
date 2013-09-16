from uibasetest import *

class TestSecurityGroup(UIBaseTest):

    security_groups = []
    port = []

    def setup(self):
        super(TestSecurityGroup, self).setup()
        self.uidriver.click_menu(self.uimap.menu_security, self.uimap.menu_security_groups)

    def test_01_create_security_group(self):
        self.uidriver.click(*self.uimap.bt_create)
        name = self.utils.generate_name(4)
        self.uidriver.enter_text(self.uimap.ed_sg_name, name)
        description = "Test"
        self.uidriver.enter_text(self.uimap.ed_sg_description, description)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        m = self.uidriver.find_row(self.uimap.tbl_security_groups, lambda r: r["NAME"] == name)
        # REVIEW: find_row returns list. So it's more correct to check that length of resulting list is 1 because theoretically it can be more than 1 and it's not good.
        ok_(m, "Security Group: %s not in list" % name)

        self.security_groups.append(name)

    def test_02_list_security_groups(self):
        # REVIEW: copy-paste error in the message string! Two times!
        ok_(len(self.security_groups) > 0, "Volume snapshot creation failed so cannot filter the list.")
        name = self.security_groups[0]
        self.uidriver.filter_table(name)
        rows = self.uidriver.parse_table(self.uimap.tbl_security_groups)
        ok_(len(rows) == 1 and rows[0]['NAME'] == name, "Failed to filter list of volume snapshots.")
        self.uidriver.filter_table('')

    def test_03_add_rule(self):
        name = self.security_groups[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_edit_rules)
        self.uidriver.select_cb_option(self.uimap.cb_ip_protocol, "TCP")
        port = self.utils.generate_port()
        self.uidriver.enter_text(self.uimap.ed_from_port, port)
        self.uidriver.enter_text(self.uimap.ed_to_port, port+1)
        # sourse_group = "default"
        sourse_group = "CIDR"
        self.uidriver.select_cb_option(self.uimap.cb_source_group, sourse_group)
        if sourse_group == "CIDR":
            cidr = "0.0.0.0/0"
            self.uidriver.enter_text(self.uimap.ed_cidr, cidr)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        # REVIEW: avoid too long lines!  I prefer lines no longer than 120 chars. PEP8 recommends 80 chars. but O think this recommendation is too old.
        row = self.uidriver.find_row(self.uimap.tbl_security_group_edit, lambda r: r["FROM PORT"] == "{0}".format(port))
        ok_(len(row) > 0, "Security Rule for port: {0} not in list".format(port))
        self.port.append(port)

    def test_04_delete_rule(self):
        ok_(len(self.port) > 0, "Rule creation failed so cannot delete the rule.")
        name = self.security_groups[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_edit_rules)
        self.uidriver.click_row_checkbox(self.uimap.tbl_security_group_edit, lambda r: r['FROM PORT'] == "{0}".format(self.port[0]))
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        row = self.uidriver.find_row(self.uimap.tbl_security_group_edit, lambda r: r["PORT"] == "{0}".format(self.port))
        ok_(len(row) == 0, "Security Rule for port: {0} not deleted".format(self.port[0]))


    def test_05_delete_security_group(self):
        # REVIEW: why anlayse port? why not security group? rule is already deleted!
        ok_(len(self.port) > 0, "Rule creation failed so cannot delete the rule.")
        name = self.security_groups[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        row = self.uidriver.find_row(self.uimap.tbl_security_groups, lambda r: r["NAME"] == "{0}".format(name))
        ok_(len(row) == 0, "Security group: {0} not deleted".format(name))

