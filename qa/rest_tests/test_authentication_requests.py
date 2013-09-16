from restbasetest import *


class TestAuthenticationRequests(RESTBaseTest):

    def test_login_logout(self):
        # login was called from setup_class method, so start from logout.
        # the complete with login to allow teardown_class to execute logout without errors.
        ok_(self.auth.logout(), "Failed to logout.")
        ok_(self.auth.login(), "Failed to login.")

if __name__ == '__main__':
    t = TestAuthenticationRequests()
    t.setup_class()
    t.test_login_logout()
    t.teardown_class()