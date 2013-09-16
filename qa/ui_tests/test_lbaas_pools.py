from uibasetest import *
from common.rest.compute_helper import InstanceHelper


class TestPools(UIBaseTest):

    pools = []
    services = []

    @classmethod
    def setup_class(cls):
        super(TestPools, cls).setup_class()
        cls.ihelper = InstanceHelper(cls.utils, cls.rest_auth)

    @classmethod
    def teardown_class(cls):
        cls.utils.cleanup_objects(cls.ihelper.terminate_instances, 'instances', id_key='instanceId')
        super(TestPools, cls).teardown_class()

    def setup(self):
        super(TestPools, self).setup()
        self.uidriver.click_menu(self.uimap.menu_lbaas, self.uimap.menu_pools)

    def test_01_create_pool(self):
        self.uidriver.click(*self.uimap.bt_add_new_pool)
        name = self.utils.generate_name(size=4)
        # 1) Enter Pool Name:
        self.uidriver.enter_text(self.uimap.ed_name, name)
        # port = self.utils.generate_digits(size=4)
        # self.uidriver.enter_text(self.uimap.ed_port, port)
        # 2) Check check box Enabled:
        self.uidriver.click(*self.uimap.chk_enabled)
        # 3) Select LB Method:
        self.uidriver.select_cb_option(self.uimap.cb_lb_method, "LeastConnection")
        # 4) Check check box Monitor:
        self.uidriver.click(*self.uimap.chk_monitors_http)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        ok_(self.uidriver.is_element_in_table(self.uimap.tbl_pools, "NAME", name), "Failed to create LBaaS pool.")
        # r = self.uidriver.find_row(self.uimap.tbl_pools, lambda r: r['NAME'] == name)
        # ok_(len(r) == 1, "Failed to create LBaaS pool.")
        self.pools.append(name)

    def test_02_list_pools(self):
        ok_(len(self.pools) == 1, 'Pool creation failed on prev. test-case. Interrupting.')
        name = self.pools[0]
        self.uidriver.filter_table(name)
        cnt_flt = int(self.uidriver.find_element(*self.uimap.counter).text)
        cnt_tbl = self.uidriver.count_rows_with_filter(self.uimap.tbl_pools, name)
        ok_(cnt_flt == cnt_tbl, "Failed to filter instance in the list.")
        # r = self.uidriver.parse_table(self.uimap.tbl_pools)
        # ok_(len(r) == 1 and r[0]['NAME'] == self.pools[0], "Failed to filter list of pools.")
        self.uidriver.filter_table('')

    def test_03_add_service(self):
        ok_(len(self.pools) == 1, 'Pool creation failed on prev. test-case. Interrupting.')
        # create instance to be added to pool
        instance = self.ihelper.create_instance()

        # add service to existing pool
        self.uidriver.click(By.LINK_TEXT, self.pools[0])
        self.uidriver.click(*self.uimap.bt_add_service)
        # 1) Select Name Instance:
        self.uidriver.select_cb_option(self.uimap.cb_instance, instance['name'])
        # 2) Select Network interface
        iface = self.uidriver.get_cb_options(self.uimap.cb_interface)[0]
        self.uidriver.select_cb_option(self.uimap.cb_interface, iface)
        # 3) Enter Port:
        port = self.utils.generate_digits(size=4)
        self.uidriver.enter_text(self.uimap.ed_port, port)
        # 4) Enter Weight:
        weight = self.utils.generate_digits(size=2)
        self.uidriver.enter_text(self.uimap.ed_weight, weight)
        # select instance and leave netInterface selected by default
        # 3) Check check box Enabled:
        if not self.uidriver.find_element(*self.uimap.chk_enabled).is_selected():
            self.uidriver.click(*self.uimap.chk_enabled)

        # service name field is automatically updated every time user changes instance or port
        # so, to keep user-defined service name, it should be typed after instance and port values specified.
        # also, service name must contain port in the end
        name = self.utils.generate_name(size=4) + ':' + port
        self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        ok_(self.uidriver.is_element_in_table(self.uimap.tbl_lbaas_services, "NAME", name), "Failed to add service to pool.")
        # r = self.uidriver.find_row(self.uimap.tbl_lbaas_services, lambda s: s['NAME'] == name)
        # ok_(len(r), "Failed to add service to pool.")
        self.services.append(name)

    def test_04_enable_disable_service(self):
        ok_(len(self.services) == 1, 'Service adding failed on prev. test-case. Interrupting.')
        # enter pool details page
        self.uidriver.click(By.LINK_TEXT, self.pools[0])
        # find checkbox for pre-created service and check it
        name = self.services[0]
        self.uidriver.click_row_checkbox(self.uimap.tbl_lbaas_services, lambda s: s['NAME'] == name)
        # self.uidriver.click_row_checkbox(self.uimap.tbl_lbaas_services,
        #                                  lambda s: s['NAME'] == name and s['ENABLED'] == 'true')
        status = self.uidriver.get_value_of_element_in_field(self.uimap.tbl_lbaas_services, "NAME", name, "ENABLED")

        def change_status(status):
            self.uidriver.click(*self.uimap.bt_confirm)
            ok_(*self.uidriver.chk_error_message())
            return True if self.uidriver.get_value_of_element_in_field(self.uimap.tbl_lbaas_services, "NAME", name, "ENABLED") != enabled else False

        if status:
            self.uidriver.click(*self.uimap.bt_disable)
            ok_(change_status(status), "Failed to disable service.")
            self.uidriver.click(*self.uimap.bt_enable)
            ok_(change_status(status), "Failed to enable service.")
        else:
            self.uidriver.click(*self.uimap.bt_enable)
            ok_(change_status(status), "Failed to enable service.")
            self.uidriver.click(*self.uimap.bt_disable)
            ok_(change_status(status), "Failed to disable service.")
        #
        # if self.uidriver.get_value_of_element_in_field(self.uimap.tbl_lbaas_services, "NAME", name, "ENABLED") == enabled:
        #
        # rule = lambda s: s['NAME'] == self.services[0] and s['ENABLED'] == 'false'
        # rows = self.uidriver.wait_for_row_change(self.uimap.tbl_lbaas_services, rule)
        # ok_(rows is not False, "Failed to disable service.")

    # def test_05_enable_service(self):
    #     ok_(len(self.services) == 1, 'Service adding failed on one of prev. test-cases. Interrupting.')
    #     self.uidriver.click(By.LINK_TEXT, self.pools[0])
    #     self.uidriver.click_row_checkbox(self.uimap.tbl_lbaas_services,
    #                                      lambda s: s['NAME'] == self.services[0] and s['ENABLED'] == 'false')
    #     self.uidriver.click(*self.uimap.bt_enable)
    #     self.uidriver.click(*self.uimap.bt_confirm)
    #     ok_(*self.uidriver.chk_error_message())
    #
    #     rule = lambda s: s['NAME'] == self.services[0] and s['ENABLED'] == 'true'
    #     rows = self.uidriver.wait_for_row_change(self.uimap.tbl_lbaas_services, rule)
    #     ok_(rows is not False, "Failed to enable service.")

    def test_06_edit_pool(self):
        ok_(len(self.pools) == 1, 'Pool creation failed on prev. test-case. Interrupting.')
        self.uidriver.click(By.LINK_TEXT, self.pools[0])
        self.uidriver.click(*self.uimap.bt_edit_pool)
        name = self.utils.generate_name(size=4)
        # 1) Enter Pool Name:
        self.uidriver.enter_text(self.uimap.ed_name, name)
        # port = self.utils.generate_digits(size=4)
        # self.uidriver.enter_text(self.uimap.ed_port, port)
        # 2) Select check box Enabled:
        if not self.uidriver.find_element(*self.uimap.chk_enabled).is_selected():
            self.uidriver.click(*self.uimap.chk_enabled)
        # 3) Select LB Method:
        self.uidriver.select_cb_option(self.uimap.cb_lb_method, "LeastConnection")
        # 4) Select check box Monitor:
        if not self.uidriver.find_element(*self.uimap.chk_monitors_http).is_selected():
            self.uidriver.click(*self.uimap.chk_monitors_http)
        # 5) Submit:
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        ok_(self.uidriver.is_element_in_table(self.uimap.tbl_pools, "NAME", name), "Failed to create LBaaS pool.")

        # r = self.uidriver.find_row(self.uimap.tbl_pools, lambda r: r['NAME'] == name)
        # ok_(len(r) == 1, "Failed to edit LBaaS pool.")
        self.pools[0] = name

    def test_07_delete_service(self):
        ok_(len(self.services) == 1, "Service wasn't created in previous CREATE test-case. Interrupting.")

        self.uidriver.click(By.LINK_TEXT, self.pools[0])
        self.uidriver.click(By.ID, "checkBox_" + self.services[0])
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)

        deleted = self.uidriver.wait_for_row_deleted(self.uimap.tbl_lbaas_services,
                                                     lambda s: s['NAME'] == self.services[0])
        ok_(deleted, "Failed to delete lbaas service.")

    def test_08_delete_pool(self):
        ok_(len(self.pools) == 1, "Pool wasn't created in previous CREATE test-case. Interrupting.")
        # delete all ui_ pools
        ui_pools = [k['name'] for k in self.utils.get_list('pools') if k['name'].startswith(self.prefix)]
        for name in ui_pools:
            self.uidriver.click_row_checkbox(self.uimap.tbl_pools, lambda r: r['NAME'] == name)
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        for name in ui_pools:
            deleted = self.uidriver.wait_for_deleted(self.uimap.tbl_pools, "NAME", name)
            ok_(deleted, "Failed to delete lbaas pool(s).")

        # deleted = self.uidriver.wait_for_row_deleted(self.uimap.tbl_pools,
        #                                              lambda s: s['NAME'].startswith(self.prefix))
        # ok_(deleted, "Failed to delete lbaas pool(s).")