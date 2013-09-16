"""
How to create image for test purpose (update/delete):
Connect to environment:
  ssh root@172.18.198.44
  pwd: swordfish
Create image(s):
  glance  image-create --disk-format=qcow2 --container-format=bare --is-public=yes  --name <img_name> < /root/cirros-0.3.0-i386-disk.img

"""

from restbasetest import *
from common.rest.compute_helper import ImageHelper


class TestImageRequests(RESTBaseTest):

    @classmethod
    def setup_class(cls):
        super(TestImageRequests, cls).setup_class()

        cls.ihelper = ImageHelper(cls.utils)

    def teardown(self):
        # after each test-case: remove image that was created
        self.utils.cleanup_objects(self.ihelper.delete_image, 'images')

    def test_list_of_images(self):
        ims = self.utils.get_list('images')
        # if get_list returned value then it is JSON object (data validated inside of get_list).
        ok_(type(ims) == list, "Unable to get list of images.")

    def test_show_image(self):
        created = self.ihelper.create_image()
        shown = self.ihelper.show_image(created['id'])
        # request 'show' for created image and verify resulting dictionary.
        ok_(created['id'] == shown['id'],
            "'Show image' failed. Expected id: %s but received %s." % (created['id'], shown['id']))

    def test_update_image(self):
        # create image
        image = self.ihelper.create_image()
        # generate new name
        new_name = self.utils.generate_string(4)
        # rename and verify renaming
        params = {'id': image['id'], 'name': new_name}
        updated = self.ihelper.update_image(params)
        ok_(updated['name'] == new_name,
            'Image renaming failed. Expected name: %s. Actual: %s' % (new_name, updated['name']))

    def test_create_delete_image(self):
        # create image
        image = self.ihelper.create_image()
        ok_(image is not False, 'Image creation failed.')  # create_image verifies if the image appeared in the list.
        # delete image
        ok_(self.ihelper.delete_image(image['id']),
            'Image deleting failed. Image with id=%s still exists.' % image['id'])

if __name__ == '__main__':
    t = TestImageRequests()
    t.setup_class()
    # t.test_update_image()
    # t.test_show_image()
    # t.teardown()
