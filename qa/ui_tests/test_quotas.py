from uibasetest import *


class TestQuotas(UIBaseTest):

    policies = []

    def setup(self):
        super(TestQuotas, self).setup()
        self.uidriver.click_menu(self.uimap.menu_settings, self.uimap.menu_quotas)

    def test_01_list_quotas(self):
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

    def test_02_edit_quotas(self):
        # rows = self.uidriver.parse_table(self.uimap.tbl_quotas)
        # ok_(len(rows) > 0, "There isn't any row in table. Interrupting.")
        ok_(self.uidriver.is_element_present(*self.uimap.counter), "There isn't any row in table. Interrupting.")

        # Store old quotas
        old_params = self.uidriver.parse_details_table(self.uimap.tbl_quotas)

        # Set new quotas
        params = {}
        self.uidriver.click(*self.uimap.bt_edit)
        for k in old_params.keys():
            params[k] = "{0}".format(eval("{0} + 1".format(old_params[k])))
            self.uidriver.enter_text(self.uimap.quota[k], params[k])
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())

        # Check new quotas
        new_params = self.uidriver.parse_details_table(self.uimap.tbl_quotas)
        for k in old_params.keys():
            ok_(params[k] == new_params[k], "Quota wasn't set: {0} != {1}".format(params[k], new_params[k]))

        # Retore old quotas
        self.uidriver.click(*self.uimap.bt_edit)
        for k in old_params.keys():
            self.uidriver.enter_text(self.uimap.quota[k], old_params[k])
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())

        # Check restoring of quotas
        params = self.uidriver.parse_details_table(self.uimap.tbl_quotas)
        for k in old_params.keys():
            ok_(params[k] == old_params[k], "Quota wasn't set: {0} != {1}".format(params[k], old_params[k]))
