from base_rest_helper import *


class InstanceHelper(BaseRESTHelper):

    def __init__(self, utils, auth):
        """
        Arguments:
          - utils: instance of Utils class
          - auth: instance of AuthenticationHelper class
        """
        super(InstanceHelper, self).__init__(utils)
        self.auth = auth

    def create_instance(self, parameters=None):
        """
        Create instance via Aurora REST API.

        Arguments:
          - parameters: dict, parameters of instance (see Wiki for details).

        Return:
          - dict, parameters of just created instance or False if creation failed.
        """
        # this nested func is used as a condition to complete waiting (see usage below)
        def find_new_instance():
            new_ins = [i for i in self.utils.get_list('instances')
                       if i['name'] == params['name'] and i['status'] != 'Build']
            if len(new_ins) == 1:
                return new_ins[0]
            else:
                return False

        # The dict enumerates only required parameters of create_instance request
        params = {
            # DETAILS
            'instanceSources': 'IMAGE',   # 'IMAGE' for image, 'SNAPSHOT' for snapshot
            'image': '',                  # existing value should be found
            'name': '',                   # generate random value
            'flavor': '',                 # existing value should be found
            'datacenter': '',             # existing value should be found
            'count': 1,
            # ACCESS AND SECURITY
            'keypair': '',                # existing value should be found
            'securityGroups': [''],       # existing value should be found
            # VOLUME OPTIONS
            'volumeOptions': 'NOT_BOOT',  # 'Do not boot from volume' item
            'deviceName': ''              # required, empty because not used
            #'snapshot': '',              # optional
            #'deleteOnTerminate': False,  # bool, optional
            # POST-CREATION
            #'customizationScript': ''    # optional
        }

        # apply non-empty user-defined parameters
        if parameters is not None:
            for k in parameters:
                if parameters[k] != "":
                    params[k] = parameters[k]

        # find and insert values for all required params that are still empty.
        if params['name'] == "":
            instances = self.utils.get_list('instances')
            params['name'] = self.utils.generate_string(4, *[i['name'] for i in instances])

        if params['image'] == "":
            for image in self.utils.get_list('images'):
                if image['status'] == 'active':
                    params['image'] = image['id']
                    break
            else:
                raise AssertionError("Unable to find active image for instance creation.")

        if params['keypair'] == "":
            pairslist = self.utils.get_list('keypairs')
            ok_(len(pairslist) > 0, "Unable to find any keypair for instance creation.")
            params['keypair'] = pairslist[0]['name']

        if params['securityGroups'] == ['']:
            groups = self.utils.get_list('securitygroups')
            ok_(len(groups) > 0, "Unable to find sec.group for instance creation.")
            params['securityGroups'] = [groups[0]['name']]

        if params['flavor'] == "":
            flavors = self.utils.get_list('flavors')
            mindisk = min(i['disk'] for i in flavors)
            for f in flavors:
                if f['disk'] == mindisk:
                    params['flavor'] = f['id']
                    break

        if params['datacenter'] == "":
            dc = self.auth.get_datacenters()
            ok_(len(dc) > 0, "Unable to find any datacenter for instance creation.")
            params['datacenter'] = dc[0]

        # launch instance creation and verify result.
        # multiple attempts to create instance - to avoid false-negative test results.
        for attempt in range(1, 4):
            print("\n=== Instance creation. Attempt # %s. ===\n" % attempt)
            # try to create instance. wait for result.
            self.utils.send_request("POST", 'create_instance', data=params)

            instance_created = self.utils.waitfor(find_new_instance, 120, 5)
            if instance_created:
                instance = find_new_instance()
            else:
                print('Timeout for instance creation expired.')

            if not instance_created or instance['status'] != 'Active':  # status can be 'Active' or 'Error'
                print("\nAttempt #" + str(attempt) + " to create 'active' instance failed." +
                      "\nThe following instances exist at the moment:\n" +
                      '\n'.join(i['name'] + ': ' + i['status'] for i in self.utils.get_list('instances')) +
                      "\nCleaning up...")

                # If unable to create instance, remove all instances except two ones.
                # If lab limit for instances is changed, modify remain value accordingly.
                self.utils.cleanup_objects(self.terminate_instances, 'instances', id_key='instanceId', remain=2)
            else:
                print('Instance created successfully.')
                return instance
        ok_(False, "Unable to create instance with 'Active' state or timeout expired.")

    def terminate_instances(self, list_of_ids):
        """
        Arguments:
          - list_of_ids: list of strings - instance ids.

        Return:
          - bool: True if success.
        """
        params = {'selectedInstances': list_of_ids}
        res = self.utils.send_request('POST', 'terminate_instances', data=params)
        # make sure the instances are not in the list anymore.
        condition = lambda: len([i for i in self.utils.get_list('instances') if i['instanceId'] in list_of_ids]) == 0
        return self.utils.waitfor(condition, 60, 3)

    def show_instance(self, id):
        """
        Arguments:
          - id: string

        Return:
          - JSON dict with instance parameters
        """
        params = {'id': id}
        res = self.utils.send_request('GET', 'show_instance', data=params)
        return json.loads(res.content)['instance']

    def pause_unpause(self, ins_id, do_pause=True):
        """
        Arguments:
          - ins_id: string
          - do_pause: bool, True to pause, False to unpause instance.

        Return:
          - JSON structure from response content field.
        """
        action = 'un' * (not do_pause) + 'pause_instance'
        params = {'instanceId': ins_id}
        res = self.utils.send_request('GET', action, data=params)
        return json.loads(res.content)

    def suspend_resume(self, ins_id, do_suspend=True):
        """
        Arguments:
          - ins_id: string
          - do_suspend: bool, True to suspend, False to resume instance.

        Return:
          - JSON structure from response content field.
        """
        action = 'suspend_instance' if do_suspend else 'resume_instance'
        params = {'instanceId': ins_id}
        res = self.utils.send_request('GET', action, data=params)
        return json.loads(res.content)

    def reboot_instance(self, ins_id):
        """
        Arguments:
          - ins_id: string

        Return:
          - JSON structure from response content field.
        """
        params = {'instanceId': ins_id}
        res = self.utils.send_request('POST', 'reboot_instance', data=params)
        return json.loads(res.content)

    def update_instance(self, ins_id, new_name):
        """
        Arguments:
          - ins_id: string
          - new_name: str, new name for instance with ins_id.

        Return:
          - JSON structure from response content field.
        """
        params = {'id': ins_id, 'name': new_name}
        res = self.utils.send_request('POST', 'update_instance', data=params)
        return json.loads(res.content)['resp']['server']

    def make_snapshot(self, ins_id, snap_name):
        """
        Create snapshot for instance.

        Arguments:
          - ins_id: string
          - snap_name: string

        Return:
          - JSON structure from response content field.
        """
        params = {'id': ins_id, 'name': snap_name}
        res = self.utils.send_request('POST', 'make_snapshot', data=params)
        return json.loads(res.content)

    def show_log(self, ins_id, showall=None):
        """
        Arguments:
          - ins_id: string
          - showall: string, optional, number of lines to be shown.

        Return:
          - JSON structure from response content field.
        """
        params = {'instanceId': ins_id, 'showAll': showall}
        res = self.utils.send_request('POST', 'show_log', data=params)
        return json.loads(res.content)


class ImageHelper(BaseRESTHelper):

    def delete_image(self, id):
        """ Delete image or instance snapshot. """
        params = {'id': id}
        res = self.utils.send_request('POST', 'delete_image', data=params)

        # make sure the image is not in the list anymore.
        def check_image_deleted():
            # collect images and snapshots
            img_snp = self.utils.get_list('images') + self.utils.get_list('instance_snapshots')
            if len([i for i in img_snp if i['id'] == id]) == 0:
                return True

        return self.utils.waitfor(check_image_deleted, 10, 2)

    def create_image(self, parameters=None):
        """
        For now the method creates image on base of fake URL.
        If 'real' image is needed - use create_image_via_cli() method.
        """
        params = {
            'name': '',
            'location': 'http://some.where.com/image.iso',
            'diskFormat': 'iso',
            'minDisk': 1,
            'minRam': 128,
            'shared': 'on'
        }
        if parameters is not None:
            for k in parameters:
                if parameters[k] != "":
                    params[k] = parameters[k]

        if params['name'] == '':
            images = self.utils.get_list('images')
            params['name'] = self.utils.generate_string(4, *[i['name'] for i in images])

        res = self.utils.send_request("POST", 'create_image', data=params)

        # return new image or raise exception if not created.
        def find_new_image():
            new_img = [i for i in self.utils.get_list('images')
                       if i['name'] == params['name'] and i['status'] == 'active']
            if len(new_img) == 1:
                return new_img[0]
            else:
                return False

        ok_(self.utils.waitfor(find_new_image, 20, 2), "Creation of image with 'active' status failed.")
        return find_new_image()

    def create_image_via_cli(self, name):
        """
        Create image via glance CLI (it's not part of Aurora functionality).
        Method expects to find image in the root directory.
        Return:
          - new image id or False if creation failed.

        """
        command = ("glance  image-create --disk-format=qcow2 "
                   "--container-format=bare --is-public=yes  "
                   "--name %s < /root/cirros-0.3.0-i386-disk.img" % name)
        res = self.utils.run_ssh_cmd(command)
        for s in res:
            parsed = s.replace('|', ' ').split()
            if 'id' in parsed:
                return parsed[1]
        return False

    def show_image(self, id):
        params = {'id': id}
        res = self.utils.send_request('GET', 'show_image', data=params)
        return json.loads(res.content)['image']

    def update_image(self, params):
        """
        Arguments:
            params dict contains the following keys:
              - id (string, required).
              - name (string, required) - new or old one.
              - shared: optional, 'on' or 'off'.

        Return:
          - JSON structure from response content field.
        """
        res = self.utils.send_request('POST', "update_image", data=params)
        return json.loads(res.content)['image']


class FlavorHelper(BaseRESTHelper):

    def create_flavor(self, parameters=None):
        params = {
            'name': '',
            'ram': 128,
            'disk': 1,
            'vcpus': 2,
            'isPublic': 'on',  # 'on' or 'off'
            'ephemeral': 1,    # optional
            'swap': 1,         # optional
            'rxtxFactor': 1    # optional
        }
        if parameters is not None:
            for k in parameters:
                if parameters[k] != "":
                    params[k] = parameters[k]

        if params['name'] == '':
            flrs = self.utils.get_list('flavors')
            params['name'] = self.utils.generate_string(4, *[f['name'] for f in flrs])

        res = self.utils.send_request("POST", 'create_flavor', data=params)
        return json.loads(res.content)['resp']['flavor']

    def delete_flavor(self, id):
        params = {'flavorId': id}
        res = self.utils.send_request('POST', 'delete_flavor', data=params)
        return json.loads(res.content)