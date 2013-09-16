from base_rest_helper import *


class PoolHelper(BaseRESTHelper):

    def create_pool(self, parameters=None):
        """
        Create lbaas pool with specified parameters.
        Almost all parameters are not required to be passed into method - default values will be assigned here.
        The only parameter that MUST be passed in parameters dict is netInterface.
        It requires to find or create valid instance to get correct netInterface,
        so it's better to do it out of this method (because it's responsible just for pool creation).
        """
        params = {
            'name': '',
            'port': '123',
            'lbMethod': '',
            'monitors': [],
            'enabled': 'on',
            'instances': [],  # list of strings, instance IDs. can be empty list.
            'netInterface': '',  # str, at least any existing net interface. can be taken from some instance.
            'servicePort': '17',
            'serviceWeight': '9'
        }
        # apply non-empty user-defined parameters
        if parameters is not None:
            for k in parameters:
                if parameters[k] != "":
                    params[k] = parameters[k]

        if params['name'] == "":
            pools = self.utils.get_list('pools')
            params['name'] = self.utils.generate_string(4, *[p['name'] for p in pools])
        if params['lbMethod'] == "":
            methods = self.utils.get_list('methods')
            params['lbMethod'] = methods[0]
        if params['monitors'] in ([], ''):
            monitors = self.utils.get_list('monitors')
            params['monitors'] = [monitors[0]]
        if params['netInterface'] == '':
            raise ValueError("netInterface value must be passed to create_pool method!")

        res = self.utils.send_request("POST", 'create_pool', data=params)
        return params  # res contains just job id, so params is returned as more useful data.

    def delete_pool(self, pools):
        params = {'selectedPools': pools}
        res = self.utils.send_request('POST', 'delete_pool', data=params)

        remaining = [p for p in self.utils.get_list('pools') if p['name'] in pools]
        return len(remaining) == 0

    def show_pool(self, poolname):
        params = {'id': poolname}
        res = self.utils.send_request('GET', 'show_pool', data=params)
        return json.loads(res.content)

    def create_service(self, parameters):
        """
        parameters dict has to define at least ip and pool keys:
          - ip: can be taken from 'networks' parameter of the instance object;
          - pool: name of pool object (its netInterface should correspond to instance's network?).

        Also, the dict can contain any other parameters needed to create a service.
        By default, service is created in disabled state.
        """
        params = {
            'name': self.utils.generate_string(4) + ":129",  # name should end with port value
            'instanceId': '',
            'netInterface': '',
            'id': '',  # pool name
            'port': '129',
            'weight': '8'
        }
        # apply non-empty user-defined parameters
        for k in parameters:
            if parameters[k] != '':
                params[k] = parameters[k]

        res = self.utils.send_request("POST", 'create_service', data=params, validate_response=True)
        return params

    def delete_service(self, parameters):
        """
        parameters dict contains two keys:
          - pool: pool name,
          - selectedServices: string of one service name or list of strings.
        """
        res = self.utils.send_request('POST', 'delete_service', data=parameters)

        services = parameters['selectedServices']
        if type(services) == str:  # convert value to list for unified filtering below
            services = [services]
        remaining = [s for s in self.show_pool(parameters['pool'])['services'] if s['name'] in services]
        return len(remaining) == 0

    def enable_service(self, parameters):
        """
        parameters dict contains two keys:
          - pool: pool name,
          - selectedServices: string of one service name or list of strings.
        """
        res = self.utils.send_request('POST', 'enable_service', data=parameters)
        # TODO: if res contains meaningful content it's not needed to analyze results here. leave it for test-case.
        services = parameters['selectedServices']
        if type(services) == str:  # convert value to list for unified filtering below
            services = [services]
        enabled = [s for s in self.show_pool(parameters['pool'])['services']
                   if s['name'] in services and s['enabled'] is True]
        return len(enabled) == len(services)

    def disable_service(self, parameters):
        """
        parameters dict contains two keys:
          - pool: pool name,
          - selectedServices: string of one service name or list of strings.
        """
        res = self.utils.send_request('POST', 'disable_service', data=parameters)
        # TODO: if res contains meaningful content it's not needed to analyze results here. leave it for test-case.
        services = parameters['selectedServices']
        if type(services) == str:  # convert value to list for unified filtering below
            services = [services]
        enabled = [s for s in self.show_pool(parameters['pool'])['services']
                   if s['name'] in services and s['enabled'] is False]
        return len(enabled) == len(services)


class VipHelper(BaseRESTHelper):

    def create_vip(self, parameters=None):
        params = {
            'name': '',
            'ip': '100.10.20.30',
            'port': 123,
            'protocol': 'HTTP',
            'enabled': 'on'
        }
        # apply non-empty user-defined parameters
        if parameters is not None:
            for k in parameters:
                if parameters[k] != "":
                    params[k] = parameters[k]
        if params['name'] == '':
            vips = self.utils.get_list('vips')
            params['name'] = self.utils.generate_string(4, *[p['name'] for p in vips])

        res = self.utils.send_request("POST", 'create_vip', data=params)
        return json.loads(res.content)

    def update_vip(self, parameters):
        """
        parameters dict has to contain the following keys:
            id: current name of the vip
            name: new name (or repeat old one if do not want to change)
            ip: new or the same ip
            port: integer
            protocol
            enabled: optional, 'on' if enabled. if disabled - do not mention this key at all.
        """
        res = self.utils.send_request('POST', 'update_vip', data=parameters)
        return json.loads(res.content)['resp']

    def delete_vip(self, vid):
        params = {'id': vid}
        res = self.utils.send_request('POST', 'delete_vip', data=params)
        remaining = [v for v in self.utils.get_list('vips') if v['name'] == vid]
        return len(remaining) == 0

    def show_vip(self, vname):
        params = {'id': vname}
        res = self.utils.send_request('GET', 'show_vip', data=params)
        return json.loads(res.content)['vip']


class PolicyHelper(BaseRESTHelper):

    def create_policy(self, parameters=None):
        params = {  # the dict can also contain optional tenantName - to create policy for non-current tenant.
            'name': '',
            'rule': 'rest rule'
        }
        # apply non-empty user-defined parameters
        if parameters is not None:
            for k in parameters:
                if parameters[k] != "":
                    params[k] = parameters[k]
        if params['name'] == '':
            policies = self.utils.get_list('policies')
            params['name'] = self.utils.generate_string(4, *[p['name'] for p in policies])

        res = self.utils.send_request("POST", 'create_policy', data=params)
        return json.loads(res.content)

    def update_policy(self, parameters):
        """
        parameters dict has to contain the following keys:
            id: current name of the policy
            name: new name (or repeat old one if do not want to change)
            rule: new or the same rule
            tenantName: optional
        """
        res = self.utils.send_request('POST', 'update_policy', data=parameters)
        return json.loads(res.content)

    def delete_policy(self, pname, tenantname=None):
        """
        Arguments:
          - id: string, policy name
          - tenantName: string, optional
        """
        params = {'id': pname}
        if tenantname is not None:
            params['tenantName'] = tenantname

        res = self.utils.send_request('POST', 'delete_policy', data=params)
        remaining = [p for p in self.utils.get_list('policies') if p['name'] == params['id']]
        return len(remaining) == 0
