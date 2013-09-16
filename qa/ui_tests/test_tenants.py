from uibasetest import *
from time import sleep


class TestTenants(UIBaseTest):

    tenants = []

    def setup(self):
        super(TestTenants, self).setup()
        self.uidriver.click_menu(self.uimap.menu_settings, self.uimap.menu_tenants)

    def test_01_create_tenants(self):
        self.uidriver.click(*self.uimap.bt_create)
        name = self.utils.generate_name(4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        description = "Test"
        self.uidriver.enter_text(self.uimap.ed_description, description)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        m = self.uidriver.find_row(self.uimap.tbl_tenants, lambda r: r["NAME"] == name)
        ok_(m, "Tenant: %s not in list" % name)

        self.tenants.append(name)

    def test_02_list_tenants(self):
        ok_(len(self.tenants) > 0, "Tenant creation failed so cannot filter the list.")
        name = self.tenants[0]
        self.uidriver.filter_table(name)
        rows = self.uidriver.parse_table(self.uimap.tbl_tenants)
        ok_(len(rows) == 1 and rows[0]['NAME'] == name, "Failed to filter list of Tenants.")
        self.uidriver.filter_table('')

    def test_03_show_tenant(self):
        ok_(len(self.tenants) > 0, "Tenant creation failed so cannot filter the list.")
        name = self.tenants[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_show_quotas)
        res = self.uidriver.is_element_present(*self.uimap.tbl_tenant_quotas)
        ok_(res, "Failed to show Tenant quotas.")
        self.uidriver.click(*self.uimap.bt_show_users)
        res = self.uidriver.is_element_present(*self.uimap.tbl_tenant_users)
        ok_(res, "Failed to show Tenant users.")

    def test_04_update_tenant(self):
        ok_(len(self.tenants) > 0, "Tenant creation failed so cannot filter the list.")
        name = self.tenants[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_edit)
        name = self.utils.generate_name(4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        description = "Test"
        self.uidriver.enter_text(self.uimap.ed_description, description)
        self.uidriver.click(*self.uimap.chk_enabled)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        self.uidriver.click(*self.uimap.bt_up)

        isOK = False
        row = self.uidriver.find_row(self.uimap.tbl_tenants, lambda r: r['NAME'] == name)
        if row:
            self.tenants[0] = name
            if row[0]['DESCRIPTION'] == description and row[0]['ENABLED'] == "true":
                isOK = True
        ok_(isOK, "Failed to update of Tenant.")

    def test_05_update_quotas_of_tenant(self):
        ok_(len(self.tenants) > 0, "Tenant creation failed so cannot filter the list.")
        name = self.tenants[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_show_quotas)
        res = self.uidriver.is_element_present(*self.uimap.tbl_tenant_quotas)
        ok_(res, "Failed to show Tenant quotas.")

        # Store old quotas
        old_params = self.uidriver.parse_details_table(self.uimap.tbl_tenant_quotas)

        # Set new quotas
        params = {}
        self.uidriver.click(*self.uimap.bt_edit_quotas)
        for k in old_params.keys():
            params[k] = "{0}".format(eval("{0} + 1".format(old_params[k])))
            self.uidriver.enter_text(self.uimap.quota[k], params[k])
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())

        # Check new quotas
        self.uidriver.click(*self.uimap.bt_show_quotas)
        new_params = self.uidriver.parse_details_table(self.uimap.tbl_tenant_quotas)
        for k in old_params.keys():
            ok_(params[k] == new_params[k], "Quota wasn't set: {0} != {1}".format(params[k], new_params[k]))

        # Retore old quotas
        self.uidriver.click(*self.uimap.bt_edit_quotas)
        for k in old_params.keys():
            self.uidriver.enter_text(self.uimap.quota[k], old_params[k])
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())

        # Check restoring of quotas
        self.uidriver.click(*self.uimap.bt_show_quotas)
        params = self.uidriver.parse_details_table(self.uimap.tbl_tenant_quotas)
        for k in old_params.keys():
            ok_(params[k] == old_params[k], "Quota wasn't set: {0} != {1}".format(params[k], old_params[k]))

    def test_06_update_users_of_tenant(self):
        ok_(len(self.tenants) > 0, "Tenant creation failed so cannot filter the list.")
        name = self.tenants[0]
        self.uidriver.click(By.LINK_TEXT, name)
        if not self.uidriver.find_element(*self.uimap.tbl_tenant_users).is_displayed():
            self.uidriver.click(*self.uimap.bt_show_users)
        res = self.uidriver.is_element_present(*self.uimap.tbl_tenant_users)
        ok_(res, "Failed to show Tenant users.")

        users = ["nova", "admin", "user"]
        self.uidriver.click(*self.uimap.bt_edit_users)
        for user in users:
            self.uidriver.click_element(self.uidriver.find_element(*self.uimap.leftSideList).find_element("id", "user_plus_{0}".format(user)))
        self.uidriver.click(*self.uimap.bt_users_roles_submit)
        # ok_(*self.uidriver.chk_error_message())
        self.uidriver.wait_for_ajax_loaded()
        if not self.uidriver.find_element(*self.uimap.tbl_tenant_users).is_displayed():
            self.uidriver.click(*self.uimap.bt_show_users)
        col_name = "USER NAME"
        col = self.uidriver.get_column_by_name(self.uimap.tbl_tenant_users, col_name)
        if len(col) > 0:
            isOk = []
            for user in users:
                isOk.append(True) if user in col else isOk.append(False)
            ok_(all(isOk), "Error: User wasn't added")
        else:
            ok_(False, "Error: Column '{0}' empty or not exist".format(col_name))
        self.uidriver.wait_for_ajax_loaded()
        if not self.uidriver.find_element(*self.uimap.tbl_tenant_users).is_displayed():
            self.uidriver.click(*self.uimap.bt_show_users)
        self.uidriver.click(*self.uimap.bt_edit_users)
        for user in users:
            self.uidriver.click_element(self.uidriver.find_element(*self.uimap.rightSideList).find_element("id", "user_minus_{0}".format(user)))
        self.uidriver.click(*self.uimap.bt_users_roles_submit)
        # ok_(*self.uidriver.chk_error_message())
        self.uidriver.wait_for_ajax_loaded()
        if not self.uidriver.find_element(*self.uimap.tbl_tenant_users).is_displayed():
            self.uidriver.click(*self.uimap.bt_show_users)
        col = self.uidriver.get_column_by_name(self.uimap.tbl_tenant_users, col_name)
        if len(col) > 0:
            isOk = []
            for user in users:
                isOk.append(False) if user in col else isOk.append(True)
            ok_(all(isOk), "Error: User wasn't deleted")

    def test_07_delete_tenant(self):
        ok_(len(self.tenants) > 0, "Tenant creation failed so cannot delete it.")
        name = self.tenants[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        row = self.uidriver.find_row(self.uimap.tbl_tenants, lambda r: r['NAME'] == name)
        ok_(len(row) == 0, "Tenant: {0} not deleted".format(name))
