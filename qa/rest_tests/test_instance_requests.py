from restbasetest import *
from common.rest.compute_helper import InstanceHelper, ImageHelper


class TestInstanceRequests(RESTBaseTest):

    @classmethod
    def setup_class(cls):
        super(TestInstanceRequests, cls).setup_class()

        cls.ihelper = InstanceHelper(cls.utils, cls.auth)
        cls.imagehelper = ImageHelper(cls.utils)

        # Number of instances to be created is limited to four. So, clean-up needed before start.
        cls.utils.cleanup_objects(cls.ihelper.terminate_instances, 'instances', id_key='instanceId')

    def teardown(self):
        # after each test-case: remove instance that was created
        self.utils.cleanup_objects(self.ihelper.terminate_instances, 'instances', id_key='instanceId')

    def test_list_of_instances(self):
        instances = self.utils.get_list('instances')
        # if get_list returned value then it is JSON object (data validated inside of get_list).
        ok_(type(instances) == list, "Unable to get list of instances.")

    def test_create_terminate_instance(self):
        instance = self.ihelper.create_instance()
        # actually, these verifications are already done in create_instance()
        # but the test should contain the checks it was created for.
        ok_(instance is not False, "Attempt to create instance failed.")
        ok_(instance['status'] == 'Active', "New instance %s was created with %s status." %
                                            (instance['name'], instance['status']))
        # terminate instance
        res = self.ihelper.terminate_instances([instance['instanceId']])
        ok_(res is True, "Instance(s) termination failed.")

    def test_show_instance(self):
        created_ins = self.ihelper.create_instance()
        ins_id = created_ins['instanceId']

        shown_ins = self.ihelper.show_instance(ins_id)
        ok_(created_ins == shown_ins, "'Show instance' failed. Expected: %s, Actual: %s." % (created_ins, shown_ins))

    def test_update_instance(self):
        # create instance
        created_ins = self.ihelper.create_instance()
        ins_id = created_ins['instanceId']

        # generate new name and rename instance
        new_name = self.utils.generate_string(6, created_ins['name'])
        updated_ins = self.ihelper.update_instance(ins_id, new_name)
        ok_(updated_ins['name'] == new_name,
            "Instance update (rename) failed. Expected name: %s but actual: %s." % (new_name, ins_id))

    def test_make_snapshot(self):
        # create instance
        created_ins = self.ihelper.create_instance()
        ins_id = created_ins['instanceId']
        # generate snapshot name
        busynames = [s['name'] for s in self.utils.get_list('instance_snapshots')]
        name = self.utils.generate_string(3, *busynames)
        # make snapshot
        res = self.ihelper.make_snapshot(ins_id, name)
        snapshots = self.utils.get_list('instance_snapshots')
        new_snapshot = [s for s in snapshots if s['name'] == name]
        # Check if snapshot created and refers to the correct instance-owner
        ok_(len(new_snapshot) == 1 and new_snapshot[0]['properties']['instance_uuid'] == ins_id,
            "Creation of snapshot for instance failed.")
        # cleanup
        self.imagehelper.delete_image(new_snapshot[0]['id'])

    def test_show_log(self):
        # create instance
        created_ins = self.ihelper.create_instance()
        ins_id = created_ins['instanceId']
        # get log and verify result
        res = self.ihelper.show_log(ins_id, showall='12')
        ok_(res['instanceId'] == ins_id and 'log' in res, "Show Log action failed.")

    def test_pause_unpause_instance(self):
        # create instance
        instance = self.ihelper.create_instance()

        ins_id = instance['instanceId']
        # pause
        self.ihelper.pause_unpause(ins_id, do_pause=True)
        condition = lambda: self.ihelper.show_instance(ins_id)['status'] == 'Paused'
        ok_(self.utils.waitfor(condition, 10, 1), "'Pause instance' failed.")

        # un-pause
        self.ihelper.pause_unpause(ins_id, do_pause=False)
        condition = lambda: self.ihelper.show_instance(ins_id)['status'] == 'Active'
        ok_(self.utils.waitfor(condition, 10, 1), "'Unpause instance' failed.")

    def test_suspend_resume_instance(self):
        # create instance
        instance = self.ihelper.create_instance()
        ins_id = instance['instanceId']
        # suspend
        self.ihelper.suspend_resume(ins_id, do_suspend=True)
        condition = lambda: self.ihelper.show_instance(ins_id)['status'] == 'Suspended'
        ok_(self.utils.waitfor(condition, 10, 1), "'Suspend instance' failed.")

        # resume
        self.ihelper.suspend_resume(ins_id, do_suspend=False)
        condition = lambda: self.ihelper.show_instance(ins_id)['status'] == 'Active'
        ok_(self.utils.waitfor(condition, 10, 1), "'Resume instance' failed.")

    def test_reboot_instance(self):
        # create instance
        instance = self.ihelper.create_instance()
        ins_id = instance['instanceId']
        # reboot
        self.ihelper.reboot_instance(ins_id)
        condition = lambda: self.ihelper.show_instance(ins_id)['status'] == 'Active'
        ok_(self.utils.waitfor(condition, 120, 5), "'Reboot instance' failed.")

# just for local debugging
if __name__ == "__main__":
    t = TestInstanceRequests()
    t.setup_class()
    # t.test_list_of_instances()
    # t.test_reboot_instance()
    # t.teardown()