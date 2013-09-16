from restbasetest import *
from common.rest.lbaas_helper import VipHelper


class TestVipRequests(RESTBaseTest):

    @classmethod
    def setup_class(cls):
        super(TestVipRequests, cls).setup_class()
        cls.vhelper = VipHelper(cls.utils)

    def teardown(self):
        self.utils.cleanup_objects(self.vhelper.delete_vip, 'vips', id_key='name')

    def test_list_of_vips(self):
        vips = self.utils.get_list('vips')
        ok_(type(vips) == list, "Unable to get list of Vips.")

    def test_create_delete_vip(self):
        vname = self.utils.generate_string(3)
        self.vhelper.create_vip({'name': vname})
        new_vips = [v for v in self.utils.get_list('vips') if v['name'] == vname]
        ok_(len(new_vips) == 1, 'Unable to create LBaaS Vip.')
        # delete and verify result
        ok_(self.vhelper.delete_vip([vname]), 'Unable to delete LBaaS Vip. Vip named "%s" still exists.' % vname)

    def test_show_vip(self):
        vname = self.utils.generate_string(3)
        self.vhelper.create_vip({'name': vname})
        shown = self.vhelper.show_vip(vname)
        ok_(shown['name'] == vname, 'Unable to show LBaaS vip.')

    # ASG-557: Alexander Bochkarev removed button "editVip", because in https://confluence.paypal.com/cnfl/display/CLOUD/LBaas+Guide i did not find information on how to update the VIP.
    # def test_update_vip(self):
    #     vname = self.utils.generate_string(3)
    #     self.vhelper.create_vip({'name': vname})
    #     vip = self.vhelper.show_vip(vname)
    #     # update ip of the vip
    #     params = {
    #         'id': vname,
    #         'name': vname,
    #         'ip': '777.777.77.77',
    #         'port': int(vip['port']),
    #         'protocol': vip['protocol']
    #     }
    #     if vip['enabled'] == 'True':
    #         params['enabled'] = 'on'
    #     self.vhelper.update_vip(params)
    #     updated_vip = self.vhelper.show_vip(vname)
    #     ok_(updated_vip['ip'] == '777.777.77.77', 'Unable to update LBaaS vip.')

if __name__ == '__main__':
    t = TestVipRequests()
    t.setup_class()
    # t.test_list_of_vips()
    t.test_update_vip()
    t.test_show_vip()
    t.teardown()