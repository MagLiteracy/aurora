from base_rest_helper import *


class AuthenticationHelper(BaseRESTHelper):

    def __init__(self, utils, labconfig):
        """
        Arguments:
          - utils: instance of Utils class
          - labconfig: dict with credentials for specific lab (taken from config file)
        """
        super(AuthenticationHelper, self).__init__(utils)

        self.env = labconfig['environment']
        self.username = labconfig['username']
        self.password = labconfig['password']

    def login(self, parameters=None):
        """
        parameters dict with any of keys (not necessary to pass all keys):
          - environment
          - username
          - password
        """
        # credentials to be used be default
        params = {
            "environment": self.env,
            "username": self.username,
            "password": self.password
        }
        if parameters is not None:
            for k, v in parameters.iteritems():
                params[k] = v

        res = self.utils.send_request("POST", 'login', data=params)
        ok_("Invalid username and/or password" not in res.content, "Login to C3 failed.")
        return True  # response contains empty dict, so it's more convenient to return bool

    def logout(self):
        self.utils.send_request("GET", 'logout')
        return True  # response contains empty dict, so it's more convenient to return bool

    def get_user_state(self):
        res = self.utils.send_request('GET', 'get_user_state')
        res = json.loads(res.content)
        return res['userState']['dataCenterName'], res['userState']['tenantId']

    def change_datacenter(self, dcname):
        res = self.utils.send_request('GET', 'change_datacenter', data={'dataCenterName': dcname})
        return json.loads(res.content)

    def change_tenant(self, ten_id):
        res = self.utils.send_request('GET', 'change_tenant', data={'tenantId': ten_id})
        return json.loads(res.content)

    def get_datacenters(self):
        """ Return list of datacenters."""
        res = self.utils.send_request('GET', 'datacenters')
        return json.loads(res.content)['keySet']
