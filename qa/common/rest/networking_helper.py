from base_rest_helper import *


class NetworkingHelper(BaseRESTHelper):

    def create_network(self, parameters=None):
        params = {
            'name': '',
            'shared': 'on',
            'adminState':  'on',
            'tenant': '',
            'external': 'on'
        }
        # apply non-empty user-defined parameters
        if parameters is not None:
            for k in parameters:
                if parameters[k] != "":
                    params[k] = parameters[k]

        # find and insert values for all required params that are still empty.
        if params['name'] == '':
            networks = self.utils.get_list('networks')
            params['name'] = self.utils.generate_string(4, *[i['displayName'] for i in networks])

        if params['tenant'] == '':
            tenants = self.utils.get_list('tenants')
            # if len(projects) == 0:
            #     projects.append(self.['volume_type'])
            if len(tenants) > 0:
                params['tenant'] = tenants[0]['name']

        res = self.utils.send_request("POST", 'create_network', data=params)

        # return new volume or raise exception if not created.
        def find_new_network():
            new_network = [x for x in self.utils.get_list('networks')
                       if x['project'] == params['name'] and x['status'] == 'active']
            if len(new_network) == 1:
                return new_network[0]
            else:
                return False
        ok_(self.utils.waitfor(find_new_network, 10, 1), "Creation of volume with 'available' status failed.")
        return find_new_network()

    def show_network(self, id):
        params = {'id': id}
        res = self.utils.send_request('GET', 'show_network', data=params)

        return json.loads(res.content)['network']

    def delete_network(self, id):
        """
        vid - single id (str) or list of ids.
        """
        params = {'selectedNetworks': id}
        res = self.utils.send_request('POST', 'delete_network', data=params)
        # volume cannot be deleted if it is attached or has a snapshot.
        res = json.loads(res.content)
        if len(res['not_deleted_ids']) > 0:
            return False

        if type(id) != list:
            id = [id]
        condition = lambda: len([x for x in self.utils.get_list('network') if v['id'] in vid]) == 0
        return self.utils.waitfor(condition, 200, 5)

#     def create_volume_type(self, name=''):
#         params = {'name': name}
#
#         if params['name'] == '':
#             types = self.utils.get_list('volumetypes')
#             params['name'] = self.utils.generate_string(4, *[i['name'] for i in types])
#
#         res = self.utils.send_request("POST", 'create_volume_type', data=params)
#
#         # return new type or raise exception if not created.
#         def find_new_type():
#             new_type = [t for t in self.utils.get_list('volumetypes') if t['name'] == params['name']]
#             if len(new_type) == 1:
#                 return new_type[0]
#             else:
#                 return False
#
#         ok_(self.utils.waitfor(find_new_type, 10, 1), "'Create volume type' failed.")
#         return find_new_type()
#
#     def show_volume_type(self, tid):
#         params = {'id': tid}
#         res = self.utils.send_request('GET', 'show_volume_type', data=params)
#         return json.loads(res.content)['volumeType']
#
#     def delete_volume_type(self, types):
#         """
#         Arguments:
#           - types: string (single id) or list of strings - types IDs.
#         """
#         params = {'selectedVolumeTypes': types}
#         res = self.utils.send_request('POST', 'delete_volume_type', data=params)
#
#         condition = lambda: len([v for v in self.utils.get_list('volumetypes') if v['id'] in types]) == 0
#         return self.utils.waitfor(condition, 10, 1)
#
#     def attach_volume(self, parameters):
#         """
#         Arguments:
#           - parameters dict contains:
#             - id: str, id of volume
#             - instance_id: string
#             - device: string, usually '/dev/vdc'
#         """
#         res = self.utils.send_request('GET', 'attach', data=parameters)
#
#         def find_attached_volume():
#             vol = [v for v in self.utils.get_list('volumes') if v['id'] == parameters['id'] and v['status'] == 'in-use']
#             if len(vol) == 1:
#                 return vol[0]
#             else:
#                 return False
#
#         ok_(self.utils.waitfor(find_attached_volume, 60, 4), "Attaching of volume failed.")
#         return find_attached_volume()
#
#     def detach_volume(self, parameters):
#         """
#         parameters keys:
#           - id: id of volume
#           - instanceId: id of instance
#         """
#         res = self.utils.send_request('GET', 'detach', data=parameters)
#
#         def find_detached_volume():
#             vol = [v for v in self.utils.get_list('volumes')
#                    if v['id'] == parameters['id'] and v['status'] == 'available']
#             if len(vol) == 1:
#                 return vol[0]
#             else:
#                 return False
#
#         ok_(self.utils.waitfor(find_detached_volume, 60, 4), "Detaching of volume failed.")
#         return find_detached_volume()
#
#
# class VolumeSnapshotHelper(BaseRESTHelper):
#
#     def create_snapshot(self, parameters=None):
#         """
#         parameters dict must contain id of volume for which the snapshot should be created;
#         also it may contain name and description of the snapshot.
#         """
#         params = {
#             'id': '',  # volume id
#             'name': '',
#             'description': 'volume snapshot description'
#         }
#         # apply non-empty user-defined parameters
#         if parameters is not None:
#             for k in parameters:
#                 if parameters[k] != "":
#                     params[k] = parameters[k]
#
#         if params['name'] == '':
#             snaps = self.utils.get_list('snapshots')
#             params['name'] = self.utils.generate_string(4, *[s['name'] for s in snaps])
#
#         res = self.utils.send_request("POST", 'create_snapshot', data=params)
#
#         # return new snapshot or raise exception if not created.
#         def find_new_snapshot():
#             new_snap = [s for s in self.utils.get_list('snapshots')
#                         if s['name'] == params['name'] and s['status'] == 'available']
#             if len(new_snap) == 1:
#                 return new_snap[0]
#             else:
#                 return False
#         ok_(self.utils.waitfor(find_new_snapshot, 10, 1), "Creation of snapshot with 'available' status failed.")
#         return find_new_snapshot()
#
#     def show_snapshot(self, id):
#         params = {'id': id}
#         res = self.utils.send_request('GET', 'show_snapshot', data=params)
#         return json.loads(res.content)['snapshot']
#
#     def delete_snapshot(self, sid):
#         params = {'id': sid}
#         res = self.utils.send_request('POST', 'delete_snapshot', data=params)
#         condition = lambda: len([s for s in self.utils.get_list('snapshots') if s['id'] == sid]) == 0
#         return self.utils.waitfor(condition, 30, 2)
