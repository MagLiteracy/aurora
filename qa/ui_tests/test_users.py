from uibasetest import *


class TestUsers(UIBaseTest):

    users = []

    def setup(self):
        super(TestUsers, self).setup()
        self.uidriver.click_menu(self.uimap.menu_settings, self.uimap.menu_users)

    def test_01_create_user(self):
        self.uidriver.click(*self.uimap.bt_create)
        name = self.utils.generate_name(size=4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        email = "{0}@testmail.com".format(name)
        self.uidriver.enter_text(self.uimap.ed_email, email)
        passwd = self.utils.generate_name(size=8)
        self.uidriver.enter_text(self.uimap.ed_pass, passwd)
        self.uidriver.enter_text(self.uimap.ed_confirm_pass, passwd)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        ok_(self.uidriver.is_element_in_table(self.uimap.tbl_users, 'USER NAME', name), "Failed to create user.")
        # res = self.uidriver.find_row(self.uimap.tbl_users, lambda r: r['USER NAME'] == name)
        # ok_(len(res) == 1, "Failed to create user.")
        self.users.append(name)

    def test_02_list_users(self):
        ok_(len(self.users) == 1, 'User creation failed on prev. test-case. Interrupting.')
        name = self.users[0]
        self.uidriver.filter_table(name)
        cnt_flt = int(self.uidriver.find_element(*self.uimap.counter).text)
        cnt_tbl = self.uidriver.count_rows_with_filter(self.uimap.tbl_users, name)
        ok_(cnt_flt == cnt_tbl, "Failed to filter user in the list.")
        # filtered = self.uidriver.parse_table(self.uimap.tbl_users)
        # ok_(len(filtered) == 1 and filtered[0]['USER NAME'] == name, "Failed to filter user in the list.")
        self.uidriver.filter_table('')

    def test_03_edit_user(self):
        name = self.users[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_edit)
        name = name + '_' + self.utils.generate_name(size=4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        email = "{0}@testmail.com".format(name)
        self.uidriver.enter_text(self.uimap.ed_email, email)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        ok_(self.uidriver.is_element_in_table(self.uimap.tbl_users, 'USER NAME', name), "Failed to update user.")
        # res = self.uidriver.find_row(self.uimap.tbl_users, lambda u: u['USER NAME'] == name)
        # ok_(len(res), "Failed to update user.")
        self.users[0] = name

    def test_04_delete_user(self):
        ui_users = [u['name'] for u in self.utils.get_list('users') if u['name'].startswith(self.prefix)]
        for name in ui_users:
            self.uidriver.click_row_checkbox(self.uimap.tbl_users, lambda r: r['USER NAME'] == name)
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        ok_(not self.uidriver.is_element_in_table(self.uimap.tbl_users, "USER NAME", name), "Failed to delete flavor(s).")
        # res = self.uidriver.find_row(self.uimap.tbl_users, lambda r: r['USER NAME'].startswith(self.prefix))
        # ok_(len(res) == 0, "Failed to delete user(s).")