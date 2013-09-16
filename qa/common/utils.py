import json
from nose.tools import ok_
from random import choice, randrange
from string import ascii_lowercase
from time import sleep
import paramiko
import string

from common.rest import urls


class Utils:

    def __init__(self, labconfig, session, prefix):
        """
        Arguments:
          - labconfig: dict with credentials for specific lab (taken from config file)
          - session: requests.Session() instance for http communication with product under test
          - prefix: string to be used to generate object names
        """
        self.labconfig = labconfig
        self.base_url = r"http://%s:%s/" % (self.labconfig['host'], self.labconfig['port'])
        self.prefix = prefix
        self.flavor = self.labconfig['flavor']
        self.keypair = self.labconfig['keypair']
        self.lab_prefix = self.labconfig['lab_prefix']
        self.suffix = self.labconfig['suffix']
        self.session = session

    def send_request(self, method, url, data=None, validate_response=True):
        """
        Wrapper for sending REST requests to make inserting workarounds possible.

        Arguments:
          - method: string ("GET"/"POST" etc).
          - url: string, key of urls dict (from rest package) or full url to send request to.
          - data: optional, dictionary of request body parameters.
          - validate_response: optional, bool, if True - check status code and content format of response.
        """
        headers = {"Content-Type": "application/json"}
        # timeout 10 sec wasn't enough for volume removing.
        timeout = 60

        if url in urls:
            full_url = self.build_url(urls[url])
        else:
            full_url = url
        resp = self.session.request(method, full_url, data=json.dumps(data), headers=headers, timeout=timeout)

        if validate_response:
            ok_(self.validate_response(resp), 'Response format is incorrect.')
        return resp

    def validate_response(self, resp, expected_code=200):
        """
        Verify if the status code and format of REST response are correct.

        Arguments:
          - resp: RESTful response object to be verified.
          - scode: int, optional, expected status code value. By default, OK status.

        Return:
          - True if both status and format are correct, False otherwise.
        """
        is_code_ok = resp.status_code == expected_code
        if not is_code_ok:
            print("Expected response status: %s but received: %s." % (expected_code, resp.status_code))
        is_body_ok = False
        try:
            json.loads(resp.content)
            is_body_ok = True
        except ValueError:
            print("Response body is not in JSON format: \n%s" % resp.content)
        return all([is_code_ok, is_body_ok])

    def get_list(self, urls_key):
        """
        Return list of objects of type defined by urls_key.
        Warning: this method does not return just response JSON but the needed part of it.
                 To get full response content send the request directly, not using this method.
        Arguments:
          - urls_key: string, key of self.urls dictionary (for ex., 'vips' or 'images').
        """
        res = self.send_request("GET", urls_key)
        content = json.loads(res.content)
        if type(content) == dict:
            # some keys in urls dict (for example, 'tenant_quotas') contain additional word
            # to differ from other key (e.g., 'quotas') but response contains the key without
            # that added word ('tenant_') so it is required to remove extra word to get a key equal to the one in resp.
            key = urls_key.split('_')[-1]  # take the last word because the extra word was added in the beginning
            # find key representing list of objects
            for k in content:
                if key in k.lower():
                    return content[k]
        else:  # in some cases response contains just list of objects
            return content

    def generate_string(self, length, *busynames):
        """
        Generate random string (starting with prefix from config file) and not equal to any string in busynames tuple.
        """
        stop = False
        while not stop:
            name = self.prefix + ''.join(choice(ascii_lowercase) for _ in range(length))
            stop = name not in busynames
        return name

    def generate_name(self, size=10, chars=string.ascii_letters+string.digits):
        return self.prefix + ''.join(choice(chars) for x in range(size))

    def generate_digits(self, size=10, chars=string.digits):
        return ''.join(choice(chars) for x in range(size))

    def generate_chars(self, size=10, chars=string.ascii_letters):
        return ''.join(choice(chars) for x in range(size))

    def generate_ip(self, net=None, ip_from=1, ip_to=254):
        if net:
            cnt = net.count('.')
            if cnt == 0:
                ip = net + ".0.0.{0}".format(randrange(ip_from, ip_to))
            elif cnt == 1:
                ip = net + ".0.{0}".format(randrange(ip_from, ip_to))
            elif cnt == 2:
                ip = net + ".{0}".format(randrange(ip_from, ip_to))
            elif cnt == 3:
                ip = net[:net.rfind('.')] + ".{0}".format(randrange(ip_from, ip_to))
            else:
                print("Invalid Net Address: {0}".format(net))
        else:
            ip = "10.0.0." + randrange(ip_from, ip_to)

        return ip

    def generate_port(self, port_from=10000, port_to=19999):
        return randrange(port_from, port_to)

    def build_url(self, relative_url):
        return self.base_url + relative_url + '.json'

    def waitfor(self, condition, timeout, period=2):
        """
        Wait for condition function to return True or timeout expires.
        Arguments:
          - condition: function returning bool value.
          - timeout, period: time in seconds
        Return:
          - True/False.
        """
        counter = 0
        while counter < timeout:
            sleep(period)
            counter += period
            if condition():
                return True
        return False

    def run_ssh_cmd(self, cmd):
        """
        Open SSH session with credentials from config file and execute command passed to the method.
        Arguments:
          - cmd: string, terminal command text
        Return:
          - stdout text
        """
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        ssh.load_system_host_keys()
        ssh.connect(hostname=self.labconfig['host'],
                    username=self.labconfig['sshuser'],
                    password=self.labconfig['sshpassword'])
        stdin, stdout, stderr = ssh.exec_command(cmd)
        res = stdout.readlines()
        print("STDOUT:\n %s" % res)
        ssh.close()
        return res

    def cleanup_objects(self, delete_obj, obj_family, name_key='name', id_key='id', remain=0):
        """
        Remove objects of the specified type. Can be applied, for ex., on start/end of test class.

        Arguments:
          - delete_obj: method obj resp. for deletion of objects of obj_family type (e.g., delete_instance);
          - obj_family: str, relative url from rest.urls dict to get the list of objects (e.g., "images");
          - name_key: str, optional. In most cases dict describing the object properties contains 'name' key
                      to define object name but sometimes this key is named differently so should be mentioned
                       explicitly in this parameter;
          - id_key: str, optional, similar to name_key - name of key defining object's id in the dict;
          - remain: int, optional, number of objects to be remained after deletion.
                    If equal to 0 than only objects which names starting with prefix are deleted.

        Example of usage::
          self.utils = Utils((labconfig, session, prefix)
          self.utils.cleanup_objects(self.snapshot_helper.delete_image, 'images', remain=2)
        """
        objects = self.get_list(obj_family)
        # delete objects created by auto-tests before
        for obj in objects:
            if obj[name_key] is not None and obj[name_key].startswith(self.prefix):  # some objects can exist w/o name
                delete_obj(obj[id_key])

        # Remove all instances with Error status
        if obj_family == 'instances':
            objects = self.get_list(obj_family)
            for obj in objects:
                if obj['status'] == 'Error':
                    delete_obj(obj[id_key])

        # If remain value specified then not only prefixed objects are deleted.
        if remain > 0:
            objects = self.get_list(obj_family)
            if len(objects) > remain:
                for obj in objects[:-remain]:
                    delete_obj(obj[id_key])