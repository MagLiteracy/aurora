from uibasetest import *
from common.rest.storage_helper import VolumeSnapshotHelper, VolumeHelper
from common.rest.compute_helper import InstanceHelper, ImageHelper


class TestVolumes(UIBaseTest):

    volumes = []

    @classmethod
    def setup_class(cls):
        super(TestVolumes, cls).setup_class()
        cls.vhelper = VolumeHelper(cls.utils)
        cls.vshelper = VolumeSnapshotHelper(cls.utils)
        cls.imagehelper = ImageHelper(cls.utils)
        cls.ihelper = InstanceHelper(cls.utils, cls.rest_auth)

        cls.utils.cleanup_objects(cls.vhelper.delete_volume, 'volumes', name_key='displayName')

    @classmethod
    def teardown_class(cls):
        cls.utils.cleanup_objects(cls.vshelper.delete_snapshot, 'snapshots')
        cls.utils.cleanup_objects(cls.vhelper.delete_volume, 'volumes', name_key='displayName')
        cls.utils.cleanup_objects(cls.ihelper.terminate_instances, 'instances', id_key='instanceId')
        super(TestVolumes, cls).teardown_class()

    def setup(self):
        super(TestVolumes, self).setup()
        self.uidriver.click_menu(self.uimap.menu_storage, self.uimap.menu_volumes)

    def test_01_create_volume(self):
        self.uidriver.click(*self.uimap.bt_create)
        name = self.utils.generate_name(size=4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.enter_text(self.uimap.ed_description, name)
        self.uidriver.enter_text(self.uimap.ed_size, '1')
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())

        rule = lambda row: row['NAME'] == name and row['STATUS'] in ('available', 'error')
        rows = self.uidriver.wait_for_row_change(self.uimap.tbl_volumes, rule, 120, 15)
        ok_(rows is not False and rows[0]['STATUS'] == 'available', "Failed to create volume in 'available' state.")
        self.volumes.append(name)

    def test_02_list_volumes(self):
        ok_(len(self.volumes) > 0, "Volume creation failed so cannot filter the list.")
        name = self.volumes[0]
        self.uidriver.filter_table(name)
        rows = self.uidriver.parse_table(self.uimap.tbl_volumes)
        ok_(len(rows) == 1 and rows[0]['NAME'] == name, "Failed to filter list of volumes.")
        self.uidriver.filter_table('')

    def test_03_attach_volume(self):
        ok_(len(self.volumes) == 1, 'Volume creation failed in prev. test-case. Interrupting.')

        im_name = self.utils.generate_name(4)
        image = self.imagehelper.create_image({'name': im_name})
        # create instance via rest request
        ins_name = self.utils.generate_name(4)
        instance = self.ihelper.create_instance({'name': ins_name, 'image': image['id']})
        ok_(instance is not False, 'Cannot create instance for volume attach testing. Interrupting.')

        # attach volume
        vol_name = self.volumes[0]
        device = "/dev/vd" + self.utils.generate_chars(4).lower()
        self.uidriver.click(By.LINK_TEXT, vol_name)
        self.uidriver.click(*self.uimap.bt_edit_attach)
        self.uidriver.select_cb_option(self.uimap.cb_attach_to_instance, ins_name)
        self.uidriver.enter_text(self.uimap.ed_device, device)
        self.uidriver.click(*self.uimap.bt_attach)
        ok_(*self.uidriver.chk_error_message())

        rule = lambda row: row['NAME'] == vol_name and row['STATUS'] == 'in-use'
        rows = self.uidriver.wait_for_row_change(self.uimap.tbl_volumes, rule, 180, 10)
        ok_(rows is not False, "Failed to attach volume to {0} on {1}".format(ins_name, device))

    def test_04_detach_volume(self):
        name = self.volumes[0]
        rule = lambda v: v['NAME'] == name and v['STATUS'] == 'in-use'
        r = self.uidriver.find_row(self.uimap.tbl_volumes, rule)
        ok_(len(r) == 1, "Failed to find volume attached in the previous test-case.")

        self.uidriver.click(By.LINK_TEXT, name)
        self.uidriver.click(*self.uimap.bt_edit_attach)
        self.uidriver.click(*self.uimap.bt_detach)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())

        rule = lambda v: v['NAME'] == name and v['STATUS'] == 'available'
        rows = self.uidriver.wait_for_row_change(self.uimap.tbl_volumes, rule, 180, 5)
        ok_(rows is not False, "Failed volume detaching.")

    def test_07_delete_volume(self):
        ok_(len(self.volumes) == 1, "Volume wasn't created in CREATE test-case. Interrupting.")
        self.uidriver.click(By.LINK_TEXT, self.volumes[0])
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())

        deleted = self.uidriver.wait_for_row_deleted(self.uimap.tbl_volumes,
                                                     lambda s: s['NAME'] == self.volumes[0])
        ok_(deleted, "Failed to delete volume %s." % self.volumes[0])