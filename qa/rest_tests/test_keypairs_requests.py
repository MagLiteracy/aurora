from restbasetest import *
from common.rest.security_helper import KeypairHelper


class TestKeypairsRequests(RESTBaseTest):

    @classmethod
    def setup_class(cls):
        super(TestKeypairsRequests, cls).setup_class()
        cls.khelper = KeypairHelper(cls.utils)

    def teardown(self):
        # remove remaining keypairs
        self.utils.cleanup_objects(self.khelper.delete_keypair, 'keypairs', id_key='name')

    def test_list_of_pairs(self):
        pairs = self.utils.get_list('keypairs')
        ok_(type(pairs) == list, "Unable to get list of keypairs.")

    def test_create_remove_keypair(self):
        # create and verify result
        created = self.khelper.create_keypair()
        pname = created['name']
        new_pairs = [p for p in self.utils.get_list('keypairs') if p['name'] == pname]
        ok_(len(new_pairs) == 1, 'Unable to create keypair.')
        # delete and verify result
        ok_(self.khelper.delete_keypair(pname), 'Unable to delete keypair. Keypair named "%s" still exists.' % pname)

    def test_import_keypair(self):
        # generate unique name
        pairs = self.utils.get_list('keypairs')
        pname = self.utils.generate_string(3, *[p['name'] for p in pairs])
        key = ("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDY6/QGRGrMQOCAlMAROANw7HwO+CtxdfGnBWIobm8+TwtmERdOV0/93WKkHW3"
               "uBI73zs1pbO3hOKftAKI7VHmjJHnShuTfnV0wgWwkIGlRXwOS7EnBomXhgWXtdtbFWZYqn9rKBlfT8HPP1McEHScyWRKpIF+Nsvr"
               "Rt/vT2n0fcC3QM/zR6oU4rSb0kgX6R4x4zsLtHKW6L16L0zOFFqjw2YsdSF8mVEEFIDnZmohnVbn7WSkyhM1ujPQscCvP9pqdhEI"
               "cBtisMmv7uPkMefBT5Wlfv35LPnuuIXvXmQxtRdZzfyZlnUqsytOZErH6+uhbSseypukSpLimq1Q0I2q9 "
               "apalanisamy@paypal.com")

        self.khelper.import_keypair({'name': pname, 'publicKey': key})
        new_pairs = [p for p in self.utils.get_list('keypairs')  if p['name'] == pname]
        ok_(len(new_pairs) == 1, "'Import keypair' failed.")

if __name__ == '__main__':
    t = TestKeypairsRequests()
    t.setup_class()
    t.test_list_of_pairs()
    t.test_create_remove_keypair()
    t.test_import_keypair()
    t.teardown()