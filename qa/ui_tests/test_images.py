from uibasetest import *
from common.rest.compute_helper import ImageHelper, InstanceHelper


class TestImages(UIBaseTest):

    images = []
    snapshots = []

    @classmethod
    def setup_class(cls):
        super(TestImages, cls).setup_class()

        cls.instancehelper = InstanceHelper(cls.utils, cls.rest_auth)
        cls.imagehelper = ImageHelper(cls.utils)

        cls.utils.cleanup_objects(cls.imagehelper.delete_image, 'images')

    @classmethod
    def teardown_class(cls):
        cls.utils.cleanup_objects(cls.imagehelper.delete_image, 'instance_snapshots')
        cls.utils.cleanup_objects(cls.imagehelper.delete_image, 'images')
        cls.utils.cleanup_objects(cls.instancehelper.terminate_instances, 'instances', id_key='instanceId')
        super(TestImages, cls).teardown_class()

    def setup(self):
        super(TestImages, self).setup()
        self.uidriver.click_menu(self.uimap.menu_compute, self.uimap.menu_images)

    def test_01_create_image(self):
        # content = self.uidriver.get_table_content(self.uimap.tbl_images)
        # col = self.uidriver.get_column_by_name(self.uimap.tbl_images, "NAME")
        # row = self.uidriver.get_row_by_element(self.uimap.tbl_images, "NAME", "C63-5")
        # val = self.uidriver.get_value_of_element_in_field(self.uimap.tbl_images, "NAME", "C63-5", "Status")
        self.uidriver.click(*self.uimap.bt_create)
        name = self.utils.generate_name(size=4)
        self.uidriver.enter_text(self.uimap.ed_name, name)

        image_location = self.config['image_location']
        self.uidriver.enter_text(self.uimap.ed_location, image_location)

        self.uidriver.click(*self.uimap.cb_format)
        self.uidriver.click(*self.uimap.select_format_iso)

        self.uidriver.enter_text(self.uimap.ed_min_disk, 1)
        self.uidriver.enter_text(self.uimap.ed_min_ram, 128)
        self.uidriver.click(*self.uimap.chk_public)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())

        res = self.uidriver.wait_for_status(self.uimap.tbl_images, "NAME", name, "STATUS", "ACTIVE")
        ok_(res, "Failed to create image.")
        # res = self.uidriver.wait_for_row_change(self.uimap.tbl_images,
        #                                         lambda r: r['NAME'] == name and r['STATUS'] == 'active')
        # ok_(res is not False and len(res) == 1, "Failed to create image.")
        self.images.append(name)

    def test_02_list_images(self):
        ok_(len(self.images) == 1, "Image wasn't created in prev. test-case. Interrupting.")
        name = self.images[0]
        self.uidriver.filter_table(name)
        cnt_flt = int(self.uidriver.find_element(*self.uimap.counter).text)
        cnt_tbl = self.uidriver.count_rows_with_filter(self.uimap.tbl_images, name)
        ok_(cnt_flt == cnt_tbl, "Failed to filter list of images.")
        #
        # rows = self.uidriver.parse_table(self.uimap.tbl_images)
        # ok_(len(rows) == 1 and rows[0]['NAME'] == name, "Failed to filter list of images.")
        self.uidriver.filter_table('')

    def test_03_show_image_details(self):
        # Verify that: 1) list of parameters is correct; 2) every parameter has a value.
        expected_parameters = ["ID:", "Name:", "Status:", "Public:", "Checksum:", "Created:",
                               "Updated:", "Container format:", "Disk format:"]
        name = self.images[0]
        self.uidriver.click(By.LINK_TEXT, name)
        details = self.uidriver.parse_details_table(self.uimap.tbl_image_details)
        self.uidriver.click(*self.uimap.bt_up)

        ok_(sorted(expected_parameters) == sorted(details.keys()),
            'Image Details page contains incorrect set of parameters. '
            'Expected:\n%s\n but found:\n%s' % (sorted(expected_parameters), sorted(details.keys())))

        empty_params = []
        for param, value in details.iteritems():
            if value == '':
                empty_params.append(param)

        ok_(len(empty_params) == 0, 'The following image parameters do not have a value assigned:\n%s' % empty_params)

    def test_04_update_image(self):
        name = self.images[0]
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_edit_image_attributes)
        name += self.utils.generate_name(size=4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        self.uidriver.click(*self.uimap.bt_up)
        ok_(self.uidriver.is_element_in_table(self.uimap.tbl_images, "NAME", name), "Failed to rename image.")
        #
        # res = self.uidriver.find_row(self.uimap.tbl_images, lambda r: r['NAME'] == name)
        # ok_(len(res) == 1, "Failed to rename image.")
        self.images[0] = name

    def test_05_update_instance_snapshot(self):
        # this method will look for active image created by somebody else and will find at least self.images[0]
        instance = self.instancehelper.create_instance()
        name = self.utils.generate_name(4)
        self.instancehelper.make_snapshot(instance['instanceId'], name)
        self.snapshots.append(name)

        self.uidriver.webdriver.refresh()
        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_edit_image_attributes)
        name += self.utils.generate_name(size=4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        self.uidriver.click(*self.uimap.bt_up)
        ok_(self.uidriver.is_element_in_table(self.uimap.tbl_snapshots, "NAME", name), "Failed to rename instance snapshot.")
        #
        # res = self.uidriver.find_row(self.uimap.tbl_snapshots, lambda r: r['NAME'] == name)
        # ok_(len(res) == 1, "Failed renaming instance snapshot.")
        #
        self.snapshots[0] = name

    def test_06_list_instance_snapshots(self):
        ok_(len(self.snapshots) == 1, "Snapshot wasn't created in prev. test-case. Interrupting.")
        name = self.snapshots[0]
        self.uidriver.filter_table(name, bottom_table=True)
        cnt_flt = int(self.uidriver.find_element(*self.uimap.counter1).text)
        cnt_tbl = self.uidriver.count_rows_with_filter(self.uimap.tbl_snapshots, name)
        ok_(cnt_flt == cnt_tbl, "Failed to filter list of snapshots.")

        # rows = self.uidriver.parse_table(self.uimap.tbl_snapshots)
        # ok_(len(rows) == 1 and rows[0]['NAME'] == name, "Failed to filter list of snapshots.")
        self.uidriver.filter_table('', bottom_table=True)

    def test_07_delete_instance_snapshot(self):
        ok_(len(self.snapshots) == 1, "Snapshot wasn't created in prev. test-case. Interrupting.")
        self.uidriver.click(By.LINK_TEXT, self.snapshots[0])
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        ok_(not self.uidriver.is_element_in_table(self.uimap.tbl_images, "NAME", self.snapshots[0]), "Failed deleting instance snapshot.")
        # done = self.uidriver.wait_for_row_deleted(self.uimap.tbl_snapshots, lambda s: s['NAME'] == self.snapshots[0])
        # ok_(done, "Failed deleting instance snapshot.")

    def test_08_delete_image(self):
        ok_(len(self.images) == 1, "Image wasn't created in CREATE test-case. Interrupting.")
        self.uidriver.click(By.LINK_TEXT, self.images[0])
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        ok_(not self.uidriver.is_element_in_table(self.uimap.tbl_images, "NAME", self.images[0]), "Failed deleting image.")

        # done = self.uidriver.wait_for_row_deleted(self.uimap.tbl_images, lambda s: s['NAME'] == self.images[0])
        # ok_(done, "Failed deleting image.")
