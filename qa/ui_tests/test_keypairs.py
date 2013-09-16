from uibasetest import *


class TestKeypairs(UIBaseTest):

    keypairs = []

    def setup(self):
        super(TestKeypairs, self).setup()
        self.uidriver.click_menu(self.uimap.menu_security, self.uimap.menu_keypairs)

    def test_01_create_keypair(self):
        self.uidriver.click(*self.uimap.bt_create)
        name = self.utils.generate_name(size=4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())

        downloaded = False
        if self.uidriver.is_element_present(*self.uimap.bt_download):
            downloaded = True

        self.uidriver.click(*self.uimap.bt_up)
        res = self.uidriver.find_row(self.uimap.tbl_keypair, lambda r: r['NAME'] == name)
        ok_(len(res) == 1, "Failed to create keypair.")
        ok_(downloaded, "There is no possibility to download the key.")
        self.keypairs.append(name)

    def test_02_list_keypairs(self):
        ok_(len(self.keypairs) > 0, "Keypair creation failed so cannot filter the list.")
        name = self.keypairs[0]
        self.uidriver.filter_table(name)
        filtered = self.uidriver.parse_table(self.uimap.tbl_keypair)
        ok_(len(filtered) == 1 and filtered[0]['NAME'] == name, "Failed to filter keypair in the list.")
        self.uidriver.filter_table('')

    def test_03_import_keypair(self):
        public_key = ("ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAtc49kmLkfsp6tvCSOrXRLzAVOIzDC9ybSjL9tqBIXdY"
                      "3qh4BPOEfPYzool7te2clGHaf7ftFwWDGCpwKUIpZBRmtVnpfzlc4mA9dycZjCbXoNs4ILOPjfQMUow"
                      "uNw65ZdSFovD1d7XDfkgZ4f3oCta8w5TyYnfCQOs/n1CgTsLKSRrDfRDpN+T3OOAj5jyzJdjvYRm4AB"
                      "cH4YzV4kFyvtduej0OwNHV8g46CpF0BnsvfWY8lTXs26dXb1grM+NQVQbu4ZNHCU/vaq/1x6f2lcFAi"
                      "sYSn9ltyaVAv9443Psh93pAurzHAl/RRztzNdiHx8EzS+D4shAZcrHjPSXA8Cw== test@testmail.com")
        self.uidriver.click(*self.uimap.bt_insert)
        name = self.utils.generate_name(size=4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.enter_text(self.uimap.ed_public_key, public_key)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())

        res = self.uidriver.find_row(self.uimap.tbl_keypair, lambda r: r['NAME'] == name)
        ok_(len(res) == 1, "Failed to import keypair.")
        self.keypairs.append(name)

    def test_04_delete_many_keypairs(self):
        # This test deletes all existing ui_ keypairs, not just those two mentioned in self.keypairs list.

        ui_keypairs = [k['name'] for k in self.utils.get_list('keypairs') if k['name'].startswith(self.prefix)]
        for name in ui_keypairs:
            self.uidriver.click_row_checkbox(self.uimap.tbl_keypair, lambda r: r['NAME'] == name)
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())

        res = self.uidriver.find_row(self.uimap.tbl_keypair, lambda r: r['NAME'].startswith(self.prefix))
        ok_(len(res) == 0, "Failed to delete keypairs.")