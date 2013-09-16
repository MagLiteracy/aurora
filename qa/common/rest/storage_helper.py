from base_rest_helper import *


class VolumeHelper(BaseRESTHelper):

    def create_volume(self, parameters=None):
        params = {
            'name': '',
            'description': 'volume description',
            'size':  1,
            'type': ''
        }
        # apply non-empty user-defined parameters
        if parameters is not None:
            for k in parameters:
                if parameters[k] != "":
                    params[k] = parameters[k]

        # find and insert values for all required params that are still empty.
        if params['name'] == '':
            volumes = self.utils.get_list('volumes')
            params['name'] = self.utils.generate_string(4, *[i['displayName'] for i in volumes])

        if params['type'] == '':
            types = self.utils.get_list('volumetypes')
            if len(types) == 0:
                types.append(self.create_volume_type()['volume_type'])
            params['type'] = types[0]['name']

        res = self.utils.send_request("POST", 'create_volume', data=params)

        # return new volume or raise exception if not created.
        def find_new_volume():
            new_vol = [v for v in self.utils.get_list('volumes')
                       if v['displayName'] == params['name'] and v['status'] == 'available']
            if len(new_vol) == 1:
                return new_vol[0]
            else:
                return False
        ok_(self.utils.waitfor(find_new_volume, 10, 1), "Creation of volume with 'available' status failed.")
        return find_new_volume()

    def show_volume(self, id):
        params = {'id': id}
        res = self.utils.send_request('GET', 'show_volume', data=params)

        return json.loads(res.content)['volume']

    def delete_volume(self, vid):
        """
        vid - single id (str) or list of ids.
        """
        params = {'selectedVolumes': vid}
        res = self.utils.send_request('POST', 'delete_volume', data=params)
        # volume cannot be deleted if it is attached or has a snapshot.
        res = json.loads(res.content)
        if len(res['not_deleted_ids']) > 0:
            return False

        if type(vid) != list:
            vid = [vid]
        condition = lambda: len([v for v in self.utils.get_list('volumes') if v['id'] in vid]) == 0
        return self.utils.waitfor(condition, 200, 5)

    def create_volume_type(self, name=''):
        params = {'name': name}

        if params['name'] == '':
            types = self.utils.get_list('volumetypes')
            params['name'] = self.utils.generate_string(4, *[i['name'] for i in types])

        res = self.utils.send_request("POST", 'create_volume_type', data=params)

        # return new type or raise exception if not created.
        def find_new_type():
            new_type = [t for t in self.utils.get_list('volumetypes') if t['name'] == params['name']]
            if len(new_type) == 1:
                return new_type[0]
            else:
                return False

        ok_(self.utils.waitfor(find_new_type, 10, 1), "'Create volume type' failed.")
        return find_new_type()

    def show_volume_type(self, tid):
        params = {'id': tid}
        res = self.utils.send_request('GET', 'show_volume_type', data=params)
        return json.loads(res.content)['volumeType']

    def delete_volume_type(self, types):
        """
        Arguments:
          - types: string (single id) or list of strings - types IDs.
        """
        params = {'selectedVolumeTypes': types}
        res = self.utils.send_request('POST', 'delete_volume_type', data=params)

        condition = lambda: len([v for v in self.utils.get_list('volumetypes') if v['id'] in types]) == 0
        return self.utils.waitfor(condition, 10, 1)

    def attach_volume(self, parameters):
        """
        Arguments:
          - parameters dict contains:
            - id: str, id of volume
            - instance_id: string
            - device: string, usually '/dev/vdc'
        """
        res = self.utils.send_request('GET', 'attach', data=parameters)

        def find_attached_volume():
            vol = [v for v in self.utils.get_list('volumes') if v['id'] == parameters['id'] and v['status'] == 'in-use']
            if len(vol) == 1:
                return vol[0]
            else:
                return False

        ok_(self.utils.waitfor(find_attached_volume, 60, 4), "Attaching of volume failed.")
        return find_attached_volume()

    def detach_volume(self, parameters):
        """
        parameters keys:
          - id: id of volume
          - instanceId: id of instance
        """
        res = self.utils.send_request('GET', 'detach', data=parameters)

        def find_detached_volume():
            vol = [v for v in self.utils.get_list('volumes')
                   if v['id'] == parameters['id'] and v['status'] == 'available']
            if len(vol) == 1:
                return vol[0]
            else:
                return False

        ok_(self.utils.waitfor(find_detached_volume, 60, 4), "Detaching of volume failed.")
        return find_detached_volume()


class VolumeSnapshotHelper(BaseRESTHelper):

    def create_snapshot(self, parameters=None):
        """
        parameters dict must contain id of volume for which the snapshot should be created;
        also it may contain name and description of the snapshot.
        """
        params = {
            'id': '',  # volume id
            'name': '',
            'description': 'volume snapshot description'
        }
        # apply non-empty user-defined parameters
        if parameters is not None:
            for k in parameters:
                if parameters[k] != "":
                    params[k] = parameters[k]

        if params['name'] == '':
            snaps = self.utils.get_list('snapshots')
            params['name'] = self.utils.generate_string(4, *[s['name'] for s in snaps])

        res = self.utils.send_request("POST", 'create_snapshot', data=params)

        # return new snapshot or raise exception if not created.
        def find_new_snapshot():
            new_snap = [s for s in self.utils.get_list('snapshots')
                        if s['name'] == params['name'] and s['status'] == 'available']
            if len(new_snap) == 1:
                return new_snap[0]
            else:
                return False
        ok_(self.utils.waitfor(find_new_snapshot, 10, 1), "Creation of snapshot with 'available' status failed.")
        return find_new_snapshot()

    def show_snapshot(self, id):
        params = {'id': id}
        res = self.utils.send_request('GET', 'show_snapshot', data=params)
        return json.loads(res.content)['snapshot']

    def delete_snapshot(self, sid):
        params = {'id': sid}
        res = self.utils.send_request('POST', 'delete_snapshot', data=params)
        condition = lambda: len([s for s in self.utils.get_list('snapshots') if s['id'] == sid]) == 0
        return self.utils.waitfor(condition, 30, 2)