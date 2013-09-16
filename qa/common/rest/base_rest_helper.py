# these imports will be used by classes-successors
import json
from nose.tools import ok_


class BaseRESTHelper(object):

    def __init__(self, utils):
        self.utils = utils