from uibasetest import *
from common.rest.compute_helper import InstanceHelper, ImageHelper


class TestInstances(UIBaseTest):

    instances = []

    @classmethod
    def setup_class(cls):
        super(TestInstances, cls).setup_class()
        # cls.ihelper = InstanceHelper(cls.utils, cls.rest_auth)
        # cls.imagehelper = ImageHelper(cls.utils)
        #
        # cls.utils.cleanup_objects(cls.ihelper.terminate_instances, 'instances', id_key='instanceId')

    @classmethod
    def teardown_class(cls):
        # cls.utils.cleanup_objects(cls.imagehelper.delete_image, 'instance_snapshots')
        # cls.utils.cleanup_objects(cls.imagehelper.delete_image, 'images')
        # cls.utils.cleanup_objects(cls.ihelper.terminate_instances, 'instances', id_key='instanceId')
        super(TestInstances, cls).teardown_class()

    def test_01_launch_instance(self):
        # content = self.uidriver.get_table_content(self.uimap.tbl_instances)
        image = self.config["image"]
        # image_name = self.utils.generate_name(4)
        # self.imagehelper.create_image({'name': image_name})

        flavor = self.utils.flavor
        # flavor = self.config["flavor"]
        # If do not want to use hard-coded flavor name - uncomment these three lines:
        # # take flavor with minimal disk
        # flavors = self.utils.get_list('flavors')
        # flavor = [f['name'] for f in flavors if f['disk'] == min(i['disk'] for i in flavors)][0]

        keypair = self.utils.keypair
        # keypair = self.config["keypair"]
        # If do not want to use hard-coded keypair name - uncomment these three lines:
        # pairslist = self.utils.get_list('keypairs')
        # ok_(len(pairslist) > 0, "Unable to find any keypair for instance creation.")
        # keypair = pairslist[0]['name']

        name = self.utils.generate_name(size=4)
        self.uidriver.click(*self.uimap.bt_launch_instance)
        self.uidriver.enter_text(self.uimap.edit_inst_name, name)

        image_list = self.uidriver.get_cb_options(self.uimap.cb_image)
        if not image in image_list:
            image = image_list[0]
        self.uidriver.select_cb_option(self.uimap.cb_image, image)
        flavor_list = self.uidriver.get_cb_options(self.uimap.cb_flavor)
        if not image in flavor_list:
            flavor = flavor_list[0]
        self.uidriver.select_cb_option(self.uimap.cb_flavor, flavor)
        self.uidriver.click(*self.uimap.bt_access_and_security)
        keypair_list = self.uidriver.get_cb_options(self.uimap.cb_keypair)
        if not keypair in keypair_list:
            keypair = keypair_list[0]
        self.uidriver.select_cb_option(self.uimap.cb_keypair, keypair)

        # TODO in future add verification of other tabs.
        if not self.uidriver.find_element(*self.uimap.chk_security_groups_default).is_selected():
            self.uidriver.click(*self.uimap.chk_security_groups_default)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())

        name = "{0}{1}{2}".format(self.utils.lab_prefix, name, self.utils.suffix)
        # define function-filter to check if instance status has been already updated in the table

        # res = self.uidriver.wait_for_status(self.uimap.tbl_instances, "NAME", name, "STATUS", "ACTIVE")
        # ok_(res, "Failed to create instance in 'Active' state.")

        rule = lambda row: row['NAME'] == name and row['STATUS'] in ('Active', 'Error')
        rows = self.uidriver.wait_for_row_change(self.uimap.tbl_instances, rule, 120, 15)
        ok_(rows is not False and rows[0]['STATUS'] == 'Active', "Failed to create instance in 'Active' state.")
        self.instances.append(name)

    def test_02_rename_instance(self):
        ok_(len(self.instances) == 1, "Instance wasn't created in prev. test-case. Interrupting.")
        name = self.instances[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_rename)
        name += self.utils.generate_name(size=4)

        self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        self.uidriver.click(*self.uimap.bt_up)

        r = self.uidriver.find_row(self.uimap.tbl_instances, lambda r: r['NAME'] == name)
        ok_(len(r) == 1, "Failed to filter instance in the list.")

        self.instances[0] = name

    def test_03_view_log(self):
        ins_name = self.instances[0]
        self.uidriver.click(By.LINK_TEXT, ins_name)
        self.uidriver.click(*self.uimap.bt_log)
        text = self.uidriver.find_element(*self.uimap.text_log).text
        ok_(len(text) > 1, "Fail! Log is empty.")

    def test_04_create_snapshot(self):
        ins_name = self.instances[0]
        self.uidriver.click(By.LINK_TEXT, ins_name)
        self.uidriver.click(*self.uimap.bt_create_snapshot)

        snapname = self.utils.generate_name(size=4)
        self.uidriver.enter_text(self.uimap.ed_name, snapname)
        self.uidriver.click(*self.uimap.bt_submit)
        # go to Images and ensure snapshot was created.
        self.uidriver.click_menu(self.uimap.menu_compute, self.uimap.menu_images)
        # res = self.uidriver.wait_for_status(self.uimap.tbl_snapshots, "NAME", snapname, "STATUS", "ACTIVE")
        # ok_(res, "Failed to create instance snapshot %s." % snapname)

        rule = lambda row: row['NAME'] == snapname and row['STATUS'] == 'active'
        r = self.uidriver.wait_for_row_change(self.uimap.tbl_snapshots, rule, 200, 10)
        ok_(r is not False and len(r) == 1, "Failed to create instance snapshot %s." % snapname)

    def test_05_vnc_console(self):
        name = self.instances[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_vnc)
        is_vnc = self.uidriver.is_element_present(By.LINK_TEXT, "click here to show only VNC")
        ok_(is_vnc, "Fail! VNC is empty.")
        self.uidriver.click(*self.uimap.bt_up)

    def test_06_pause_instance(self):
        name = self.instances[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_pause)
        self.uidriver.webdriver.refresh()
        self.uidriver.click(*self.uimap.bt_up)
        # res = self.uidriver.wait_for_status(self.uimap.tbl_instances, "NAME", name, "STATUS", "PAUSED")
        # ok_(res, "Failed to create instance snapshot %s." % name)

        rule = lambda row: row['NAME'] == name and row['STATUS'] == 'Paused'
        rows = self.uidriver.wait_for_row_change(self.uimap.tbl_instances, rule, 300, 15)
        ok_(rows is not False, "Failed instance pausing.")

    def test_07_unpause_instance(self):
        name = self.instances[0]
        self.uidriver.click(By.LINK_TEXT, name)
        if self.uidriver.is_element_present(*self.uimap.bt_unpause):
            self.uidriver.click(*self.uimap.bt_unpause)
        else:
            ok_(False, "Instance pausing failed in the prev. test-case. Interrupting.")
        self.uidriver.webdriver.refresh()
        self.uidriver.click(*self.uimap.bt_up)
        # res = self.uidriver.wait_for_status(self.uimap.tbl_instances, "NAME", name, "STATUS", "ACTIVE")
        # ok_(res, "Failed to create instance snapshot %s." % name)

        rule = lambda row: row['NAME'] == name and row['STATUS'] == 'Active'
        rows = self.uidriver.wait_for_row_change(self.uimap.tbl_instances, rule, 300, 15)
        ok_(rows is not False, "Failed instance unpausing.")

    def test_08_suspend_instance(self):
        name = self.instances[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_suspend)
        self.uidriver.webdriver.refresh()
        self.uidriver.click(*self.uimap.bt_up)
        # res = self.uidriver.wait_for_status(self.uimap.tbl_instances, "NAME", name, "STATUS", "SUSPENDED")
        # ok_(res, "Failed to create instance snapshot %s." % name)

        rule = lambda row: row['NAME'] == name and row['STATUS'] == 'Suspended'
        rows = self.uidriver.wait_for_row_change(self.uimap.tbl_instances, rule, 300, 15)
        ok_(rows is not False, "Failed instance suspending.")

    def test_09_resume_instance(self):
        name = self.instances[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_resume)
        self.uidriver.webdriver.refresh()
        self.uidriver.click(*self.uimap.bt_up)
        # res = self.uidriver.wait_for_status(self.uimap.tbl_instances, "NAME", name, "STATUS", "ACTIVE")
        # ok_(res, "Failed to create instance snapshot %s." % name)

        rule = lambda row: row['NAME'] == name and row['STATUS'] == 'Active'
        rows = self.uidriver.wait_for_row_change(self.uimap.tbl_instances, rule, 300, 15)
        ok_(rows is not False, "Failed instance resuming.")

    def test_10_reboot_instance(self):
        name = self.instances[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_reboot)
        self.uidriver.click(*self.uimap.bt_confirm)
        self.uidriver.click(*self.uimap.bt_up)
        # res = self.uidriver.wait_for_status(self.uimap.tbl_instances, "NAME", name, "STATUS", "ACTIVE")
        # ok_(res, "Failed to create instance snapshot %s." % name)

        rule = lambda row: row['NAME'] == name and row['STATUS'] == 'Active'
        rows = self.uidriver.wait_for_row_change(self.uimap.tbl_instances, rule, 300, 15)
        ok_(rows is not False, "Failed instance rebooting.")

    def test_11_list_instances(self):
        ok_(len(self.instances) > 0, "Instance creation failed so cannot filter the list.")
        name = self.instances[0]
        ok_(self.uidriver.filter_table(name), "Skipped: Filter isn't present.")
        cnt_flt = int(self.uidriver.find_element(*self.uimap.counter).text)
        cnt_tbl = self.uidriver.count_rows_with_filter(self.uimap.tbl_instances, name)
        ok_(cnt_flt == cnt_tbl, "Failed to filter instance in the list.")

        # filtered = self.uidriver.parse_table(self.uimap.tbl_instances)
        # ok_(len(filtered) == 1 and filtered[0]['NAME'] == name, "Failed to filter instance in the list.")
        self.uidriver.filter_table('')

    def test_12_terminate_instance(self):
        ui_instances = [f['name'] for f in self.utils.get_list('instances') if f['name'].startswith(self.prefix)]
        for name in ui_instances:
            self.uidriver.click_row_checkbox(self.uimap.tbl_instances, lambda r: r['NAME'] == name)
        self.uidriver.click(*self.uimap.bt_terminate_instance)
        self.uidriver.click(*self.uimap.bt_confirm)

        for name in self.instances:
            # deleted = self.uidriver.wait_for_deleted(self.uimap.tbl_instances, "NAME", name)
            # ok_(deleted, "Failed to terminate instance(s).")

            deleted = self.uidriver.wait_for_row_deleted(self.uimap.tbl_instances,
                                                         lambda r: r['NAME'].startswith(self.prefix), 300, 15)
            ok_(deleted, "Failed to terminate instance(s).")

if __name__ == '__main__':
    t = TestInstances()
    t.setup_class()
    t.setup()
    t.test_02_rename_instance()
    t.teardown()
    t.teardown_class()