from base_rest_helper import *


class TenantHelper(BaseRESTHelper):

    def create_tenant(self, parameters=None):
        """
        Arguments:
          - parameters: dict with any of three possible parameters for tenant creation.
        Return:
          - dictionary with tenant parameters.
        """
        params = {
            'name': '',
            'description': 'qa tenant description',
            'enabled': 'on'  # optional, 'on' or 'off'
        }
        # apply non-empty user-defined parameters
        if parameters is not None:
            for k in parameters:
                if parameters[k] != "":
                    params[k] = parameters[k]

        if params['name'] == "":
            tenants = self.utils.get_list('tenants')
            params['name'] = self.utils.generate_string(4, *[t['name'] for t in tenants])

        res = self.utils.send_request("POST", 'create_tenant', data=params)
        return json.loads(res.content)['resp']

    def show_tenant(self, tenant_id):
        """
        Arguments:
          - tenant_id: string, id of tenant.
        Return:
          - dictionary with tenant parameters.
        """
        params = {'id': tenant_id}
        res = self.utils.send_request('POST', 'show_tenant', data=params)
        return json.loads(res.content)

    def delete_tenant(self, tenant_id):
        params = {'id': tenant_id}
        res = self.utils.send_request('POST', 'delete_tenant', data=params)
        return json.loads(res.content)  # almost empty dict

    def update_tenant(self, parameters):
        """
        Arguments:
          - parameters dict has to contain the following keys:
            - id - str, specify which tenant to update;
            - name - required, new value or repeat the old one if do not want to change;
            - description - required, new value or repeat the old one if do not want to change;
            - enabled - optional, new state value: 'on' or 'off'.
        Return:
          - dictionary with tenant parameters.
        """
        res = self.utils.send_request('POST', 'update_tenant', data=parameters)
        return json.loads(res.content)['resp']

    def get_tenant_quotas(self, tenant_id):
        """ Return list of quota dictionaries. """
        params = {'id': tenant_id}
        res = self.utils.send_request('POST', 'tenant_quotas', data=params)
        return json.loads(res.content)['quotas']

    def update_tenant_quotas(self, parameters):
        """
        parameters dict has to contain at least one key - 'id' of tenant.
        Also it may contain values (integers) for any of 13 quotas (cores, key_pairs, etc).
        If some quota is not specified, its old value is applied.
        Return:
          - dict of quotas.
        """
        current_quotas = self.get_tenant_quotas(parameters['id'])
        quotas = {}
        for q in current_quotas:
            qname = q['name']
            if qname in parameters['quota']:
                quotas[qname] = parameters['quota'][qname]
            else:
                quotas[qname] = int(q['limit'])

        params = {'id': parameters['id'], 'quota': quotas}

        res = self.utils.send_request('POST', 'update_tenant_quotas', data=params)
        return json.loads(res.content)['resp']

    def __get_roles_users_tenantusers(self, tenant_id):
        """
        "tenant_users" request returns three pieces of data:
          - users of the specified tenant,
          - all existing roles,
          - all existing users.

        So, this method hides common code for receiving each of pieces.
        Return:
          - dict with 3 keys representing all users, all roles and users (with their roles) of specified tenant.
        """
        params = {'id': tenant_id}
        res = self.utils.send_request('GET', 'tenant_users', data=params)
        return json.loads(res.content)

    def get_tenant_users(self, tenant_id):
        """
        Return dict where:
          keys - user IDs
          values - dicts {user's role id, role name}.
        """
        res = self.__get_roles_users_tenantusers(tenant_id)
        return res['usersRoles']

    def get_all_roles(self, tenant=None):
        """
        Return list of roles. Each role is a dict with role_id and role_name keys.
        """
        if tenant is None:
            tenant = self.utils.get_list('tenants')[0]
        res = self.__get_roles_users_tenantusers(tenant['id'])
        return res['roles']

    def get_all_users(self, tenant=None):
        """
        Return list of all users. Each user is a dict with user id, name and other keys.
        """
        if tenant is None:
            tenant = self.utils.get_list('tenants')[0]
        res = self.__get_roles_users_tenantusers(tenant['id'])
        return res['users']

    def update_tenant_users(self, parameters):
        """
        Arguments:
            parameters dict with keys:
              - tenantId (string, required)
              - newUsersRoles: json string - dict {user_name: role_id} for all users.
        """
        res = self.utils.send_request('POST', 'update_tenant_users', data=parameters)
        return json.loads(res.content)

    def get_tenant_policies(self, tenant_name):
        """
        Return dict with policies.
        """
        params = {'tenantName': tenant_name}
        res = self.utils.send_request('GET', 'tenant_policies', data=params)
        return json.loads(res.content)


class UserHelper(BaseRESTHelper):

    def __init__(self, utils):
        super(UserHelper, self).__init__(utils)
        self.thelper = TenantHelper(utils)

    def create_user(self, parameters=None):
        params = {
            'name': '',
            'email': '',
            'password': '123456',
            'confirm_password': '123456',
            'role_id': '',
            'tenant_id': ''
        }
        # apply non-empty user-defined parameters
        if parameters is not None:
            for k in parameters:
                if parameters[k] != "":
                    params[k] = parameters[k]

        if params['name'] == "":
            users = self.utils.get_list('users')
            params['name'] = self.utils.generate_string(4, *[t['name'] for t in users])
        if params['email'] == '':
            params['email'] = params['name'] + '@rest.com'
        if params['tenant_id'] == '':
            tenant = self.thelper.create_tenant()
            params['tenant_id'] = tenant['id']
        if params['role_id'] == '':
            role = self.thelper.get_all_roles(tenant)[0]
            params['role_id'] = role['id']

        res = self.utils.send_request("POST", 'create_user', data=params)

        new_user = [u for u in self.thelper.get_all_users() if u['name'] == params['name']]
        return new_user[0]  # dict with user attributes. res is useless so new_user retrieved for return.

    def show_user(self, user_id):
        """
        user_id: string, id of user.
        """
        params = {'id': user_id}
        res = self.utils.send_request('POST', 'show_user', data=params)
        return json.loads(res.content)['user']  # user dictionary returns

    def delete_user(self, user_ids):
        """
        user_ids - list of strings
        """
        params = {'selectedUsers': user_ids}
        res = self.utils.send_request('POST', 'delete_user', data=params)
        return json.loads(res.content)  # almost empty dict returns

    def update_user(self, parameters):
        """
        Arguments:
          - parameters dict has to contain the following keys:
            - id - str, specify which user to update;
            - name, email, password, confirm_password, tenant_id -
              required fields, set new value or repeat the old one if do not want to change.
        """
        res = self.utils.send_request('POST', 'update_user', data=parameters)
        return json.loads(res.content)['resp']  # updated user returns
