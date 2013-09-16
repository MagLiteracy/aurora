from uibasetest import *


class TestQuotaUsage(UIBaseTest):

    policies = []

    def setup(self):
        super(TestQuotaUsage, self).setup()
        self.uidriver.click_menu(self.uimap.menu_settings, self.uimap.menu_quota_usage)

    def test_01_list_quota_usage(self):
        counter = 0
        if self.uidriver.is_element_present(*self.uimap.counter):
            counter = int(self.uidriver.find_element(*self.uimap.counter).text)
        ok_(counter > 0, "There isn't any row in table. Interrupting.")
        rows = self.uidriver.parse_table(self.uimap.tbl_quotas, limit=1)
        name = rows[0]["NAME"]
        self.uidriver.filter_table(name)

        counter = int(self.uidriver.find_element(*self.uimap.counter).text)
        ok_(counter == 1, "Failed to filter list of quotas.")

        rows = self.uidriver.parse_table(self.uimap.tbl_quotas)
        ok_(rows[0]['NAME'] == name, "Failed to filter list of quotas.")
        self.uidriver.filter_table('')

    def test_02_edit_quota_usage(self):
        # rows = self.uidriver.parse_table(self.uimap.tbl_quotas)
        # ok_(len(rows) > 0, "There isn't any row in table. Interrupting.")
        ok_(self.uidriver.is_element_present(*self.uimap.counter), "There isn't any row in table. Interrupting.")

        # Store old quotas
        param_names = ["Cores", "Instances", "Ram"]
        # Set new quotas
        self.uidriver.click(*self.uimap.bt_edit)
        old_params = {}
        params = {}
        for k in param_names:
            old_params[k] = self.uidriver.find_element(*self.uimap.quota[k]).get_attribute('value')
            if old_params[k].isdigit():
                params[k] = "{0}".format(int(old_params[k]) + 1)
                self.uidriver.enter_text(self.uimap.quota[k], params[k])
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())

        # Check new quotas
        self.uidriver.click(*self.uimap.bt_edit)
        for k in param_names:
            value = self.uidriver.find_element(*self.uimap.quota[k]).get_attribute('value')
            ok_(value == params[k], "Quota wasn't set: {0} != {1}".format(value, params[k]))

        # Retore old quotas
        for k in param_names:
            self.uidriver.enter_text(self.uimap.quota[k], old_params[k])
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())

        # Check restoring of quotas
        self.uidriver.click(*self.uimap.bt_edit)
        for k in param_names:
            value = self.uidriver.find_element(*self.uimap.quota[k]).get_attribute('value')
            ok_(value == old_params[k], "Quota wasn't set: {0} != {1}".format(value, old_params[k]))
        self.uidriver.click(*self.uimap.bt_up)