package asgard

import com.paypal.asgard.ImageService
import com.paypal.asgard.OpenStackRESTService
import com.paypal.asgard.model.Image
import grails.test.mixin.TestFor
import org.apache.http.Header
import org.apache.http.message.BufferedHeader
import org.apache.http.util.CharArrayBuffer
import org.apache.shiro.SecurityUtils
import org.apache.shiro.util.ThreadContext
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(ImageService)
class ImageServiceTest extends GMockTestCase {

    static final IMAGES = 'images'
    static final GLANCE = 'image'
    static final NOVA = 'compute'

    static final String X_META_IMAGE = 'X-Image-Meta'

    static final image1 = [id: 'id1', name: 'imageZ', properties: [image_type: 'image']]
    static final image2 = [id: 'id2', name: 'imageA']
    static final snapshot1 = [id: 'id3', name: 'snapshotZ',
            properties: [image_type: 'snapshot']]
    static final snapshot2 = [id: 'id4', name: 'snapshotA', properties: [image_type: 'snapshot']]

    class Mysubject {
        def getPrincipal(){
            "iamauser"
        }
        def isAuthenticated(){
            true
        }
        def static hasRole(String val){
            return true
        }
    }

    @Before
    void setUp() {
        OpenStackRESTService openStackRESTService = mock(OpenStackRESTService);
        service.openStackRESTService = openStackRESTService
        service.openStackRESTService.GLANCE.returns(GLANCE).stub()
        service.openStackRESTService.NOVA.returns(NOVA).stub()

        service.openStackRESTService.get(GLANCE, 'images/detail').returns([images: [image1, image2, snapshot1, snapshot2]]).stub()
        def subject = Mysubject

        ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY,[ getSubject: { subject } ] as SecurityManager )

        SecurityUtils.metaClass.static.getSubject = { subject }

    }

    def testGetAllImages() {
        play {
            assertEquals([new Image(image2), new Image(image1)], service.getAllImages())
        }
    }

    def testGetAllSnapshots() {
        play {
            assertEquals([new Image(snapshot2), new Image(snapshot1)], service.getAllInstanceSnapshots())
        }
    }

    def testDeleteImageById() {
        service.openStackRESTService.delete(NOVA, 'images/id1').returns(null).stub()

        play {
            assertNull(service.deleteImageById('id1'))
        }
    }

    static final imageAsMap = [
            id: image1.id,
            name: image1.name,
            is_public: true,
            disk_format: 'qcow2',
            status: 'active',
            created_at: '2013-07-15T08:53:28',
            updated_at: '2013-07-17T15:10:59',
            min_disk: 0,
            min_ram: 0,
            container_format: 'bare',
            checksum: '90169ba6f09b5906a7f0755bd00bf2c3',
            size: 9159168,
            owner: 'ff97c30c7ed64f75894d3ef876dc8732',
            protected: false,
            deleted: false,
            deleted_at: null,
            properties: [:]
    ]

    static final snapshotAsMap = [
            checksum: "feaf66c179d68d716312436b535eefc3",
            container_format: "bare",
            created_at: "2013-08-23T09:34:44",
            disk_format: "qcow2",
            id: snapshot1.id,
            min_disk: "0",
            min_ram: "0",
            name: snapshot1.name,
            properties: [
                base_image_ref: "8af83b6e-a3f8-4b3c-a7ad-abcd5c3d7ca3",
                image_location: "snapshot",
                image_state: "available",
                image_type: "snapshot",
                instance_uuid: "c42c9bf0-74d2-446d-805b-cc262de6aad3",
                kernel_id: null,
                owner_id: "ff97c30c7ed64f75894d3ef876dc8732",
                ramdisk_id: null,
                user_id: "9ee0ef857a0e408886f00c8b2541576f"
            ],
            is_public: "false",
            status: "active",
            type: "snapshot",
            updated_at: "2013-08-23T09:34:51"
    ]

    static headers1 = "Content-Type: text/html; charset=UTF-8,Content-Length: 0," +
            "X-Image-Meta-Id: $image1.id," +
            "X-Image-Meta-Name: $image1.name," +
            "X-Image-Meta-Is_public: True," +
            "X-Image-Meta-Disk_format: $imageAsMap.disk_format," +
            "X-Image-Meta-Status: $imageAsMap.status," +
            "X-Image-Meta-Created_at: $imageAsMap.created_at," +
            "X-Image-Meta-Updated_at: $imageAsMap.updated_at," +
            "X-Image-Meta-Min_disk: $imageAsMap.min_disk," +
            "X-Image-Meta-Min_ram: $imageAsMap.min_ram," +
            "X-Image-Meta-Container_format: $imageAsMap.container_format," +
            "X-Image-Meta-Checksum: $imageAsMap.checksum," +
            "X-Image-Meta-Size: $imageAsMap.size," +
            "X-Image-Meta-Owner: $imageAsMap.owner," +
            "X-Image-Meta-Protected: False," +
            "X-Image-Meta-Deleted: False," +
            "Location: http://172.18.198.44:9292/v1/images/1a351087-e520-4d2e-9578-6bb533b46193," +
            "Etag: 90169ba6f09b5906a7f0755bd00bf2c3," +
            "X-Openstack-Request-Id: req-2da86077-2c2c-4ac0-b123-bf9ea721b1e2,Date: Fri, 19 Jul 2013 08:59:17 GMT,Connection: keep-alive"

    static headers2 = """Content-Type: text/html; charset=UTF-8, Content-Length: 0,
            X-Image-Meta-Property-User_id: $snapshotAsMap.properties.user_id,
            X-Image-Meta-Status: active,
            X-Image-Meta-Property-Image_state: $snapshotAsMap.properties.image_state,
            X-Image-Meta-Owner: ff97c30c7ed64f75894d3ef876dc8732,
            X-Image-Meta-Name: $snapshot1.name,
            X-Image-Meta-Container_format: bare,
            X-Image-Meta-Property-Image_type: $snapshotAsMap.properties.image_type,
            X-Image-Meta-Created_at: 2013-08-23T09:34:44,
            X-Image-Meta-Property-Image_location: $snapshotAsMap.properties.image_location,
            X-Image-Meta-Min_ram: 0,
            X-Image-Meta-Updated_at: 2013-08-23T09:34:51,
            X-Image-Meta-Id: $snapshot1.id,
            X-Image-Meta-Property-Instance_uuid: $snapshotAsMap.properties.instance_uuid,
            X-Image-Meta-Deleted: False,
            X-Image-Meta-Checksum: feaf66c179d68d716312436b535eefc3,
            X-Image-Meta-Protected: False,
            X-Image-Meta-Min_disk: 0,
            X-Image-Meta-Size: 13041664,
            X-Image-Meta-Property-Base_image_ref: $snapshotAsMap.properties.base_image_ref,
            X-Image-Meta-Is_public: False,
            X-Image-Meta-Property-Owner_id: $snapshotAsMap.properties.owner_id,
            X-Image-Meta-Disk_format: qcow2,
            Location: http://172.18.198.44:9292/v1/images/697d5f11-6818-4c32-879c-36f70c1fb7c9,
            Etag: feaf66c179d68d716312436b535eefc3,
            X-Openstack-Request-Id: req-65f60599-f6c2-4785-9822-a4e6bac856fa,
            Date: Wed, 04 Sep 2013 11:23:31 GMT, Connection: keep-alive"""

    static Header[] makeHeaders(def headersAsString) {
        def headers = [];

        headersAsString.split(',').each {
            def buffer = new CharArrayBuffer(128)
            buffer.append(it.trim())
            headers << new BufferedHeader(buffer)
        }

        headers.toArray(new Header[0])
    }

    def testGetImageById() {
        service.openStackRESTService.head(GLANCE, "${IMAGES}/$image1.id").returns(makeHeaders(headers1)).stub()
        service.openStackRESTService.head(GLANCE, "${IMAGES}/$snapshot1.id").returns(makeHeaders(headers2)).stub()

        play {
            assertEquals(new Image(imageAsMap), service.getImageById(image1.id))
            assertEquals(new Image(snapshotAsMap), service.getImageById(snapshot1.id))
        }
    }

    def testCreateImage() {

        def params = [name: imageAsMap.name, shared: imageAsMap.is_public?'on':'off', minDisk: imageAsMap.min_disk, minRam: imageAsMap.min_ram, diskFormat: imageAsMap.disk_format, location: 'URL']
        def tokens = ["${X_META_IMAGE}-name": imageAsMap.name, "${X_META_IMAGE}-is_public": imageAsMap.is_public,
                "${X_META_IMAGE}-container_format": 'bare', "${X_META_IMAGE}-min_disk":imageAsMap.min_disk,
                "${X_META_IMAGE}-min_ram": imageAsMap.min_ram, "${X_META_IMAGE}-disk_format": imageAsMap.disk_format]

        service.openStackRESTService.post(GLANCE, "${IMAGES}", null, tokens).returns([image: imageAsMap]).stub()
        service.openStackRESTService.put(GLANCE, "${IMAGES}/${imageAsMap.id}", ['x-glance-api-copy-from': params.location]).returns([image: imageAsMap]).stub()

        play {
            assertEquals(new Image(imageAsMap), service.createImage(params))
        }

    }


    def testUpdateImage() {
        def tokens = ["${X_META_IMAGE}-name": image1.name, "${X_META_IMAGE}-is_public": imageAsMap.is_public, 'x-glance-registry-purge-props': false]
        service.openStackRESTService.put(GLANCE, "${IMAGES}/${image1.id}", tokens).returns([image: imageAsMap]).stub()

        play {
            assertEquals([image: imageAsMap], service.updateImage([name:image1.name, shared: 'on', id: image1.id]))
        }
    }

    def testExists() {
        play {
            assertTrue(service.exists(image1.name))
            assertFalse(service.exists('123'))
        }
    }

}
