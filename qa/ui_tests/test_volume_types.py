from uibasetest import *


class TestVolumeTypes(UIBaseTest):

    volume_types = []

    def setup(self):
        super(TestVolumeTypes, self).setup()
        self.uidriver.click_menu(self.uimap.menu_storage, self.uimap.menu_volumes)

    def test_01_create_volume_type(self):
        self.uidriver.click(*self.uimap.bt_create_volume_type)
        name = self.utils.generate_name(size=4)
        self.uidriver.enter_text(self.uimap.ed_name, name)
        self.uidriver.click(*self.uimap.bt_submit)
        ok_(*self.uidriver.chk_error_message())
        res = self.uidriver.find_row(self.uimap.tbl_volume_types, lambda r: r['NAME'] == name)
        ok_(len(res) == 1, "Failed to create volume type.")
        self.volume_types.append(name)

    def test_02_list_volume_types(self):
        # Verification of list filtering (for now).
        ok_(len(self.volume_types) > 0, "Volume type creation failed so cannot filter the list.")
        name = self.volume_types[0]
        self.uidriver.filter_table(name, bottom_table=True)
        filtered = self.uidriver.parse_table(self.uimap.tbl_volume_types)
        ok_(len(filtered) == 1 and filtered[0]['NAME'] == name, "Failed to filter volume type in the list.")
        self.uidriver.filter_table('', bottom_table=True)

    def test_03_delete_volume_type(self):
        ui_volume_types = [f['name'] for f in self.utils.get_list('volumetypes') if f['name'].startswith(self.prefix)]
        for name in ui_volume_types:
            self.uidriver.click_row_checkbox(self.uimap.tbl_volume_types, lambda r: r['NAME'] == name)
        self.uidriver.click(*self.uimap.bt_delete_volume_type)
        self.uidriver.click(*self.uimap.bt_confirm)
        ok_(*self.uidriver.chk_error_message())
        res = self.uidriver.find_row(self.uimap.tbl_volume_types, lambda r: r['NAME'].startswith(self.prefix))
        ok_(len(res) == 0, "Failed to delete volume type(s).")