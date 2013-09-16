from uibasetest import *


class TestVips(UIBaseTest):

    vips = []

    def setup(self):
        super(TestVips, self).setup()
        self.uidriver.click_menu(self.uimap.menu_lbaas, self.uimap.menu_vips)

    def test_01_create_vip(self):
        self.uidriver.click(*self.uimap.bt_create)
        name = self.utils.generate_name(size=4)
        self.uidriver.enter_text(self.uimap.ed_ip, '192.168.0.1')
        self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.select_cb_option(self.uimap.cb_protocol, 'TCP')
        port = self.utils.generate_digits(size=4)
        self.uidriver.enter_text(self.uimap.ed_port, port)
        self.uidriver.click(*self.uimap.chk_enabled)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())

        r = self.uidriver.find_row(self.uimap.tbl_vips, lambda r: r['VIP NAME'] == name)
        ok_(len(r) == 1, "Failed to create LBaaS vip.")
        self.vips.append(name)

    def test_02_list_vips(self):
        ok_(len(self.vips) == 1, 'Vip creation failed on prev. test-case. Interrupting.')
        self.uidriver.filter_table(self.vips[0])

        rows = self.uidriver.parse_table(self.uimap.tbl_vips)
        ok_(len(rows) == 1 and rows[0]['VIP NAME'] == self.vips[0], "Failed to filter list of vips.")
        self.uidriver.filter_table('')

    # ASG-557: Alexander Bochkarev removed button "editVip", because in https://confluence.paypal.com/cnfl/display/CLOUD/LBaas+Guide i did not find information on how to update the VIP.
    # def test_03_update_vip(self):
    #     ok_(len(self.vips) == 1, 'Vip creation failed on CREATE test-case. Interrupting.')
    #     self.uidriver.click(By.LINK_TEXT, self.vips[0])
    #     self.uidriver.click(*self.uimap.bt_edit_vip)
    #
    #     name = self.vips[0] + self.utils.generate_name(size=4)
    #     ip = "192.168.0.2"
    #     port = self.utils.generate_digits(size=4)
    #     self.uidriver.enter_text(self.uimap.ed_ip, ip)
    #     self.uidriver.enter_text(self.uimap.ed_name, name)
    #     self.uidriver.enter_text(self.uimap.ed_port, port)
    #     self.uidriver.click(*self.uimap.bt_submit)
    #     self.uidriver.click(*self.uimap.bt_up)
    #
    #     r = self.uidriver.find_row(self.uimap.tbl_vips, lambda r: r['VIP NAME'] == name)
    #     ok_(len(r) == 1, "Failed to update name of LBaaS vip.")
    #     ok_(r['IP'] == ip and r['PORT'] == port, "Failed to update IP or port of LBaaS vip.")
    #     self.vips[0] = name

    def test_04_delete_vip(self):
        ui_vips = [f['name'] for f in self.utils.get_list('vips') if f['name'].startswith(self.prefix)]
        for name in ui_vips:
            self.uidriver.click_row_checkbox(self.uimap.tbl_vips, lambda r: r['VIP NAME'] == name)
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())

        deleted = self.uidriver.wait_for_row_deleted(self.uimap.tbl_vips,
                                                     lambda s: s['VIP NAME'].startswith(self.prefix))
        ok_(deleted, "Failed to delete LBaaS vip(s).")