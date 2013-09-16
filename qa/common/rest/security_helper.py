from base_rest_helper import *


class SecurityGroupHelper(BaseRESTHelper):

    def create_security_group(self, parameters=None):
        """
        Arguments:
          - parameters: dict, parameters of sec. group (see Wiki for details).

        Return:
          - dict, parameters of just created group.
        """
        params = {
            'name': '',
            'description': 'REST auto-test',  # optional
        }
        # apply non-empty user-defined parameters
        if parameters is not None:
            for k in parameters:
                if parameters[k] != "":
                    params[k] = parameters[k]
        if params['name'] == "":
            groups = self.utils.get_list('securitygroups')
            params['name'] = self.utils.generate_string(4, *[i['name'] for i in groups])

        # launch group creation and verify result (inside of send_request)
        res = self.utils.send_request("POST", 'create_security_group', data=params)
        return json.loads(res.content)['resp']

    def delete_security_group(self, gid):
        """
        Arguments:
          - gid: string - group id.

        Return:
          - bool: True if success.
        """
        params = {'id': gid}
        res = self.utils.send_request('POST', 'delete_security_group', data=params)
        remaining = [i for i in self.utils.get_list('securitygroups') if i['id'] == gid]
        return len(remaining) == 0

    def show_security_group(self, id):
        """
        Arguments:
          - id: string

        Return:
          - JSON dict with group parameters
        """
        params = {'id': id}
        res = self.utils.send_request('GET', 'show_security_group', data=params)

        return json.loads(res.content)['securityGroup']

    def add_rule(self, group_id):
        params = {
            'id': group_id,
            'ipProtocol': 'TCP',
            'fromPort': 1,  # int
            'toPort': 2,  # int
            'sourceGroup': group_id,  # one of sec. groups
            # 'cidr': ''  # optional. format: 'ip/mask' where ip - from 0.0.0.0 to 255.255.255.255, mask 0 to 32
        }
        res = self.utils.send_request('GET', 'add_rule', data=params)
        return json.loads(res.content)['resp']['security_group_rule']

    def delete_rule(self, selected_rules):
        params = {'selectedRules': selected_rules}  # selected_rules can be str or list of str.
        res = self.utils.send_request('POST', 'delete_rule', data=params)
        return json.loads(res.content)


class KeypairHelper(BaseRESTHelper):

    def create_keypair(self, parameters=None):
        params = {'name': ''}
        # apply non-empty user-defined parameters
        if parameters is not None:
            for k in parameters:
                if parameters[k] != "":
                    params[k] = parameters[k]
        if params['name'] == "":
            pairs = self.utils.get_list('keypairs')
            params['name'] = self.utils.generate_string(3, *[p['name'] for p in pairs])

        res = self.utils.send_request('POST', 'create_keypair', data=params)
        return json.loads(res.content)['keypair']

    def delete_keypair(self, pairname):
        params = {'keypairName': pairname}
        res = self.utils.send_request('DELETE', 'delete_keypair', data=params)
        # res usually contains not very useful [None] so return True/False result.
        remaining = [p for p in self.utils.get_list('keypairs') if p['name'] == pairname]
        return len(remaining) == 0

    def import_keypair(self, params):
        """
        Arguments:
          - params: dict {'name' '<value>', 'publicKey': '<value>'}
        """
        res = self.utils.send_request('POST', 'import_keypair', data=params)
        return json.loads(res.content)


