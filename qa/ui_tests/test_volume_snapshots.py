from uibasetest import *
from common.rest.storage_helper import VolumeSnapshotHelper, VolumeHelper


class TestVolumeSnapshots(UIBaseTest):

    vol_snapshots = []

    @classmethod
    def setup_class(cls):
        super(TestVolumeSnapshots, cls).setup_class()
        cls.vhelper = VolumeHelper(cls.utils)
        cls.vshelper = VolumeSnapshotHelper(cls.utils)

        cls.utils.cleanup_objects(cls.vshelper.delete_snapshot, 'snapshots')

    @classmethod
    def teardown_class(cls):
        cls.utils.cleanup_objects(cls.vshelper.delete_snapshot, 'snapshots')
        cls.utils.cleanup_objects(cls.vhelper.delete_volume, 'volumes', name_key='displayName')
        super(TestVolumeSnapshots, cls).teardown_class()

    def setup(self):
        super(TestVolumeSnapshots, self).setup()
        self.uidriver.click_menu(self.uimap.menu_storage, self.uimap.menu_snapshots)

    def test_01_create_volume_snapshot(self):
        vname = self.utils.generate_name(4)
        volume = self.vhelper.create_volume({'name': vname})
        ok_(volume is not False, 'Volume creation failed so cannot create its snapshot. Interrupting.')

        self.uidriver.click_menu(self.uimap.menu_storage, self.uimap.menu_volumes)
        self.uidriver.click(By.LINK_TEXT, vname)
        self.uidriver.click(*self.uimap.bt_create)
        snapshot_name = self.utils.generate_name(size=4)
        self.uidriver.enter_text(self.uimap.ed_name, snapshot_name)
        self.uidriver.enter_text(self.uimap.ed_description, snapshot_name + '_description')

        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        self.uidriver.filter_table(filter_text='')

        rows = self.uidriver.wait_for_row_change(self.uimap.tbl_volume_snapshots,
                                                 lambda s: s['NAME'] == snapshot_name and
                                                           s['STATUS'] == 'available')
        ok_(rows is not False or len(rows) == 1, "Failed to create volume snapshot.")

        self.vol_snapshots.append(snapshot_name)

    # def test_02_list_volume_snapshots(self):
    #     ok_(len(self.vol_snapshots) > 0, "Volume snapshot creation failed so cannot filter the list.")
    #     name = self.vol_snapshots[0]
    #     self.uidriver.filter_table(name)
    #     rows = self.uidriver.parse_table(self.uimap.tbl_volume_snapshots)
    #     ok_(len(rows) == 1 and rows[0]['NAME'] == name, "Failed to filter list of volume snapshots.")
    #
    #     self.uidriver.filter_table('')

    def test_03_launch_instance_from_volume_snapshot(self):
        # todo For now this test does not create instance because maybe it's out of scope. It just verifies if the correct default values are applied.

        ok_(len(self.vol_snapshots) == 1, "Volume snapshot wasn't created in CREATE test-case. Interrupting.")
        self.uidriver.click(By.LINK_TEXT, self.vol_snapshots[0])
        self.uidriver.click(*self.uimap.bt_create)
        instance_name = self.utils.generate_name(4)
        self.uidriver.enter_text(self.uimap.edit_inst_name, instance_name)
        self.uidriver.click(*self.uimap.bt_volume_options)
        vol_opt = self.uidriver.get_cb_options(self.uimap.cb_volume_options)
        # ok_(self.uidriver.get_selected_cb_option(self.uimap.cb_volume_options) ==
        #     'Boot from volume snapshot (creates a new volume)',
        #     "Wrong volume option selected by default.")

        # self.uidriver.select_cb_option(self.uimap.cb_volume_options, "Boot from volume")
        self.uidriver.select_cb_option(self.uimap.cb_volume_options, vol_opt[1])
        volumes = self.uidriver.get_cb_options(self.uimap.cb_volume_snapshot)
        if self.vol_snapshots[0] in volumes:
            self.uidriver.select_cb_option(self.uimap.cb_volume_snapshot, self.vol_snapshots[0])

        ok_(self.uidriver.get_selected_cb_option(self.uimap.cb_volume_snapshot) == self.vol_snapshots[0],
            "Wrong volume snapshot selected by default.")

    def test_04_delete_volume_snapshot(self):
        ok_(len(self.vol_snapshots) == 1, "Volume snapshot wasn't created in CREATE test-case. Interrupting.")
        self.uidriver.click(By.LINK_TEXT, self.vol_snapshots[0])
        self.uidriver.click(*self.uimap.bt_delete)
        self.uidriver.click(*self.uimap.bt_confirm)

        deleted = self.uidriver.wait_for_row_deleted(self.uimap.tbl_volume_snapshots,
                                                     lambda s: s['NAME'] == self.vol_snapshots[0])
        ok_(deleted, "Failed to delete volume snapshot %s." % self.vol_snapshots[0])
