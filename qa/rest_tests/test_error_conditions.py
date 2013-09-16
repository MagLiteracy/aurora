from restbasetest import *
from common.rest import urls


class TestErrorResponses(RESTBaseTest):

    # Selected URLs represent most of entities, are not dangerous for the SUT, utilize different methods.
    test_urls = {
        # AUTHENTICATION
        'login': ('POST',),
        # INSTANCES
        'create_instance': ('POST',),
        'show_instance': ('GET', 'POST'),
        'terminate_instances': ('POST',),
        'reboot_instance': ('GET', 'POST'),
        # IMAGES
        'delete_image': ('GET', 'POST'),
        # FLAVORS
        'delete_flavor': ('GET', 'POST'),
        # SECURITY
        'create_security_group': ('POST',),
        'delete_security_group': ('DELETE', 'POST'),
        # KEYPAIRS
        'create_keypair': ('GET', 'POST'),
        'delete_keypair': ('DELETE', 'POST'),
        'import_keypair': ('POST',),
        # VOLUMES
        'create_volume': ('POST',),
        'attach': ('GET', 'POST'),
        # VOLUME SNAPSHOTS
        'show_snapshot': ('GET', 'POST'),
        # SETTINGS.TENANTS
        'show_tenant': ('GET', 'POST'),
        # SETTINGS.USERS
        'show_user': ('GET', 'POST'),
        # Heat
        'create_stack': ('POST',),
        'show_stack': ('GET', 'POST'),
        'upload': ('POST',),
        # LBaaS VIPs
        'create_vip': ('GET', 'POST'),
        'update_vip': ('GET', 'POST'),
        # LBaaS Policies
        'create_policy': ('GET', 'POST'),
        'delete_policy': ('POST',)
    }

    def teardown(self):
        self.auth.logout()
        self.auth.login()

    def test_incorrect_login_credentials(self):
        self.auth.logout()  # precondition

        # Login with wrong password (for other parameters default values will be used)
        params = {
            "username": self.auth.username,
            "environment": self.auth.env,
            "password": 'incorrect_pwd'}
        res = self.utils.send_request("POST", 'login', data=params, validate_response=False)
        try:
            content = json.loads(res.content)
        except ValueError:
            ok_(False, 'Response to %s should be in JSON format.' % urls['login'])

        ok_(res.status_code == 401, "Status code is wrong. Expected 401 but received %s." % res.status_code)
        ok_('dataCenterErrors' in content, "Response content is wrong.")

    def test_incorrect_param_format(self):
        # Send every available request with incorrect parameter(s).
        # Expected result: status code in (200, 500).
        # Response content in JSON format, maybe even dict with "error" or "errors" key.

        params = {'incorrect_parameter': 123}
        passed = True
        for k, v in self.test_urls.iteritems():
            method = v[0]
            res = self.utils.send_request(method, k, data=params, validate_response=False)

            if res.status_code not in (500, 200):
                passed = False
                print "Incorrect status code for %s: %s." % (k, res.status_code)

            try:
                content = json.loads(res.content)
                if not (type(content) is dict and ('error' in content or 'errors' in content)):
                    print("Response to %s should be a dictionary containing 'error[s]' key. "
                          "Actual value: \n%s" % (urls[k], content))
                    passed = False
            except ValueError:
                print('Response to %s should be in JSON format. Actual value:\n%s' % (urls[k], res.content))
                passed = False

        ok_(passed, "Requests with incorrect body parameters have to get error message in response.")

    def test_incorrect_request_url(self):
        url = self.utils.build_url('incorrect_relative_url')
        res = self.utils.send_request('GET', url, validate_response=False)
        ok_(res.status_code == 404, "Status code is wrong. Expected 404 but received %s." % res.status_code)
        try:
            content = json.loads(res.content)
        except ValueError:
            ok_(False, "Format of response content has to be JSON. Actual result: \n%s" % res.content)

    def test_incorrect_request_method(self):
        # For all urls predefined in setup_class, try to send request with unsupported methods.
        # Expected result: error 405, JSON structure in resp. content.

        results = dict.fromkeys(self.test_urls, True)

        for u, methods in self.test_urls.iteritems():
            for method in ('GET', 'POST', 'PUT', 'DELETE'):
                if method in methods:
                    continue
                res = self.utils.send_request(method, u, validate_response=False)
                if res.status_code != 405:
                    print("Request %s: %s method was not rejected. "
                          "Expected status: %s but received - 200" % (urls[u], method, 405))
                    results[u] = False

                try:
                    json.loads(res.content)
                except ValueError:
                    print("Request %s, method %s: resp. content should be in JSON format. Actual value:\n%s" %
                          (urls[u], method, res.content))
                    results[u] = False

        ok_(all(results.values()), "Incorrect request methods are not rejected.")

if __name__ == "__main__":
    t = TestErrorResponses()
    t.setup_class()
    t.test_incorrect_login_credentials()
    t.test_incorrect_param_format()
    # t.test_incorrect_request_url()
    # t.test_incorrent_request_method()
    t.teardown_class()