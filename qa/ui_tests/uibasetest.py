import sys
import os
# add root directory to PythonPath to allow importing from common package to test scripts
tests_dir = os.path.dirname(os.path.realpath(__file__))
qa_root = os.path.dirname(tests_dir)
sys.path.insert(0, qa_root)

import requests
import yaml

from testconfig import config
from nose.tools import ok_
from selenium.webdriver.common.by import By

from fw.uimap import UIMap
from common.utils import Utils
from fw.uidriver import UIDriver
from fw.login_helper import LoginHelper
from common.rest.authentication_helper import AuthenticationHelper as RestAuth


class UIBaseTest(object):

    @classmethod
    def setup_class(cls):
        os.chdir(os.path.dirname(os.path.realpath(__file__)))
        cls.config = config

        if not cls.config:  # if not file passed - can used for local debugging.
            cls.config = yaml.load(open("cfg.yaml"))

        cls.prefix = cls.config["prefix"]

        cls.uimap = UIMap()

        cls.session = requests.Session()
        labconfig = cls.config[cls.config['lab']]
        cls.utils = Utils(labconfig, cls.session, cls.prefix)

        cls.uidriver = UIDriver(cls.config, cls.uimap, cls.utils)
        cls.login_helper = LoginHelper(cls.uidriver)
        cls.rest_auth = RestAuth(cls.utils, labconfig)

        cls.rest_auth.login()

    @classmethod
    def teardown_class(cls):
        cls.uidriver.stop_webdriver()
        cls.rest_auth.logout()

    def setup(self):
        self.login_helper.login()

    def teardown(self):
        #if, for example, Error 500 page was shown
        if self.uidriver.is_element_present('xpath', '//*[contains(text(), "Exception Message")]'):
            self.uidriver.webdriver.back()
        self.login_helper.logout()