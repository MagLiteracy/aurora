import sys
import os
# add root directory to PythonPath to allow importing from common package to test scripts
tests_dir = os.path.dirname(os.path.realpath(__file__))
qa_root = os.path.dirname(tests_dir)
sys.path.insert(0, qa_root)

import yaml
import requests
from testconfig import config

# the following import are needed for classes-descendants
import json
from time import sleep
from nose.tools import ok_

from common.utils import Utils
from common.rest.authentication_helper import AuthenticationHelper


class RESTBaseTest(object):

    @classmethod
    def setup_class(cls):
        # Set current dir to allow PyCharm launching nosetests and resolve imports successfully
        os.chdir(os.path.dirname(os.path.realpath(__file__)))

        data = config  # get configuration dictionary passed as cmd line argument.
        if not data:  # if no cmd arg passed - read the file. convenient for local debugging.
            data = yaml.load(open("config.yaml"))
        labconfig = data[data['lab']]

        cls.session = requests.Session()
        cls.utils = Utils(labconfig, cls.session, data['prefix'])
        cls.auth = AuthenticationHelper(cls.utils, labconfig)

        cls.auth.login()

    @classmethod
    def teardown_class(cls):
        cls.auth.logout()
        cls.session.close()

