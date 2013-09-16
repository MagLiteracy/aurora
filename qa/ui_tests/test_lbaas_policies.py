from uibasetest import *


class TestPolicies(UIBaseTest):

    policies = []

    def setup(self):
        super(TestPolicies, self).setup()
        self.uidriver.click_menu(self.uimap.menu_lbaas, self.uimap.menu_policies)

    def test_01_create_policy(self):
        # create 4 policies to test Delete in different ways
        for i in range(4):
            self.uidriver.click(*self.uimap.bt_create)
            name = self.utils.generate_name(size=4)
            self.uidriver.enter_text(self.uimap.ed_name, name)
            rule = self.utils.generate_chars(size=40)
            self.uidriver.enter_text(self.uimap.ed_rule, rule)
            self.uidriver.click(*self.uimap.bt_submit)
            ok_(*self.uidriver.chk_error_message())

            r = self.uidriver.find_row(self.uimap.tbl_policies, lambda r: r['NAME'] == name)
            ok_(len(r) == 1, "Failed to create LBaaS policy.")
            self.policies.append(name)

    def test_02_list_policies(self):
        ok_(len(self.policies) > 0, 'Policy creation failed on prev. test-case. Interrupting.')
        self.uidriver.filter_table(self.policies[0])

        rows = self.uidriver.parse_table(self.uimap.tbl_policies)
        ok_(len(rows) == 1 and rows[0]['NAME'] == self.policies[0], "Failed to filter list of policies.")
        self.uidriver.filter_table('')

    def test_03_update_policy(self):
        ok_(len(self.policies) > 0, 'Policy creation failed on CREATE test-case. Interrupting.')
        self.uidriver.click(By.LINK_TEXT, self.policies[0])

        name = self.policies[0] + self.utils.generate_chars(size=4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())

        r = self.uidriver.find_row(self.uimap.tbl_policies, lambda r: r['NAME'] == name)
        ok_(len(r) == 1, "Failed to update name of LBaaS policy.")
        self.policies[0] = name

    def test_04_delete_one_policy(self):
        ok_(len(self.policies) > 0, 'Policy creation failed on CREATE test-case. Interrupting.')
        name = self.policies[0]
        self.uidriver.click_row_checkbox(self.uimap.tbl_policies, lambda r: r['NAME'] == name)
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        deleted = self.uidriver.wait_for_row_deleted(self.uimap.tbl_policies, lambda s: s['NAME'] == name)
        ok_(deleted, "Failed to delete one LBaaS policy.")

    def test_05_delete_policy_from_details_page(self):
        name = self.policies[1]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_delete_policy)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        deleted = self.uidriver.wait_for_row_deleted(self.uimap.tbl_policies, lambda s: s['NAME'] == name)
        ok_(deleted, "Failed to delete LBaaS policy from Details page.")

    def test_06_delete_multiple_policies(self):
        # Remove all ui_ policies. It will cover at least 2 remaining policies from self.policies list.
        ui_policies = [f['name'] for f in self.utils.get_list('policies') if f['name'].startswith(self.prefix)]
        for name in ui_policies:
            self.uidriver.click_row_checkbox(self.uimap.tbl_policies, lambda r: r['NAME'] == name)
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        deleted = self.uidriver.wait_for_row_deleted(self.uimap.tbl_policies,
                                                     lambda s: s['NAME'].startswith(self.prefix))
        ok_(deleted, "Failed to delete multiple LBaaS policies.")

