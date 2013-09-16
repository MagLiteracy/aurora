package asgard

import com.paypal.asgard.InstanceService
import com.paypal.asgard.OpenStackRESTService
import com.paypal.asgard.VolumeService
import com.paypal.asgard.QuotaService
import com.paypal.asgard.exception.RestClientRequestException
import com.paypal.asgard.model.Instance
import com.paypal.asgard.model.Quota
import com.paypal.asgard.model.Volume
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(VolumeService)
class VolumeServiceTest extends GMockTestCase {

    final static NOVA_VOLUME = "volume"
    final static NOVA = "compute"

    final static created_volume  = [id:"id",
            availability_zone: "zone",
            status: "creating",
            volume_type: "vol-type",
            size: 1,
            bootable: true,
            display_name: "display name1",
            created_at: "date_as_string",
            display_description: "description",
            attachments: [],
            metadata: [:],
            source_volid: null
    ]

    final static volume1 = [id:"id1",
        availability_zone: "zone1",
        "os-vol-host-attr:host": "host1",
        status: "status1",
        volume_type: "vol-type1",
        snapshot_id: "snapshot-id1",
        size: 1,
        bootable: true,
        display_name: "display name1",
        "os-vol-tenant-attr:tenant_id": "tenant-id1",
        created_at: "date_as_string1",
        display_description: "description1",
        attachments: [server_id: ["instanceId1"], device: ['/dev/vda']],
        metadata: [:],
        source_volid: null
    ]
    final static volume2 = [id:"id2",
            availability_zone: "zone2",
            "os-vol-host-attr:host": "host2",
            status: "status2",
            volume_type: "vol-type2",
            snapshot_id: "snapshot-id2",
            size: 1,
            bootable: true,
            display_name: "display name2",
            "os-vol-tenant-attr:tenant_id": "tenant-id2",
            created_at: "date_as_string2",
            display_description: "description2",
            attachments: [],
            metadata: [:],
            source_volid: null
    ]

    final static instance1 = [id: 'instanceId1',
            name: 'instanceName1',
            instanceSources: 'Image',
            status: 'Active',
            volumeOptions: 'Do not boot from volume',
            images: 'cirros_ClAz_SFMC_oiIT_oO4g_Uc71_0iJ1_3Wml',
            count: 1,
            flavor: [id: 'flavorId', name: 'flavorName'],
            image: [id: 'imageId', name: 'imageName']
    ]

    final static volumeType1 = [id: 'volumeTypeId1', name: 'volumeTypeName1', extra_specs: [:]]
    final static volumeType2 = [id: 'volumeTypeId2', name: 'volumeTypeName2', extra_specs: [:]]

    def volumeWithInstance



    @Before
    void setUp() {
        OpenStackRESTService openStackRESTService = mock(OpenStackRESTService);
        service.openStackRESTService = openStackRESTService
        service.openStackRESTService.NOVA_VOLUME.returns(NOVA_VOLUME).stub()
        service.openStackRESTService.NOVA.returns(NOVA).stub()

        service.instanceService = mock(InstanceService)


        service.quotaService = mock(QuotaService)
        volumeWithInstance = new Volume(volume1)
        volumeWithInstance.instanceName = instance1.name

    }

    def testGetAllVolumes() {
        service.openStackRESTService.get(NOVA_VOLUME, "volumes/detail").returns([volumes: [volume1, volume2]]).stub()
        service.instanceService.getById('instanceId1', false).returns(new Instance(instance1)).stub()
        play {
            assertEquals([volumeWithInstance, new Volume(volume2)], service.getAllVolumes())
        }
    }

    def testGetAllVolumesWithException() {
        service.openStackRESTService.get(NOVA_VOLUME, "volumes/detail").returns([volumes: [volume1, volume2]]).stub()
        service.instanceService.getById('instanceId1', false).raises(new RestClientRequestException("123")).stub()
        play {
            assertEquals([new Volume(volume1), new Volume(volume2)], service.getAllVolumes())
        }
    }

    def testGetVolumeById() {
        service.openStackRESTService.get(NOVA_VOLUME, "volumes/$volume1.id").returns([volume: volume1]).times(1)
        service.openStackRESTService.get(NOVA_VOLUME, "volumes/$volume2.id").returns([volume: volume2]).times(1)
        service.instanceService.getById('instanceId1', false).returns(new Instance(instance1)).stub()

        play {
            assertEquals(volumeWithInstance, service.getVolumeById(volume1.id))
            assertEquals(new Volume(volume2), service.getVolumeById(volume2.id))
        }
    }

    def testDeleteVolumeById() {
        service.openStackRESTService.delete(NOVA_VOLUME, "volumes/$volume1.id").returns(null).stub()
        play {
            assertNull(service.deleteVolumeById(volume1.id))
        }
    }
    def testCreateVolume() {
        def body = [volume: [status: "creating", availability_zone: null, source_volid: null,
                display_description: created_volume.description, snapshot_id: null, user_id: null, size: created_volume.size, display_name: created_volume.name,
                imageRef: null, attach_status: "detached", volume_type: created_volume.volume_type, project_id: null, metadata: []]]

        service.openStackRESTService.post(NOVA_VOLUME, "volumes", body).returns([volume: created_volume]).stub()

        play {
            assertEquals([volume: created_volume], service.createVolume([name: created_volume.name, description: created_volume.description, size: created_volume.size, type: created_volume.volume_type]))
        }
    }

    def testUpdateVolume() {
        def body = [volume: [display_name: created_volume.name, display_description: created_volume.description]]
        service.openStackRESTService.put(NOVA_VOLUME, "volumes/$created_volume.id", body).returns([volume: created_volume]).stub()
        play {
            assertEquals([volume: created_volume], service.updateVolume(created_volume))
        }
    }

    def testAttachToInstance() {
        def body = [volumeAttachment: [device: volume1.attachments.device[0], volumeId: volume1.id]]
        def volumeAttachment = [id: volume1.id, volumeId: volume1.id, device: volume1.attachments.device[0], serverId: volume1.attachments.server_id[0]]
        service.openStackRESTService.post(NOVA, "servers/${volume1.attachments.server_id[0]}/os-volume_attachments", body).returns([volumeAttachment: volumeAttachment]).stub();

        play {
            assertEquals([volumeAttachment: volumeAttachment], service.attachToInstance([device: volume1.attachments.device[0], id:volume1.id, instanceId: volume1.attachments.server_id[0]]))
        }
    }

    def testDetachFromInstance() {
        service.openStackRESTService.delete(NOVA, "servers/${volume1.attachments.server_id[0]}/os-volume_attachments/$volume1.id").returns(null).stub()
        play {
            assertNull(service.detachFromInstance([id:volume1.id, instanceId: volume1.attachments.server_id[0]]))
        }
    }

    def testGetAllVolumeTypes() {
        service.openStackRESTService.get(NOVA_VOLUME, "types").returns([volume_types: [volumeType1, volumeType2]]).stub()
        play {
            assertEquals([volumeType1, volumeType2], service.getAllVolumeTypes())
        }
    }

    def testCreateVolumeType() {
        service.openStackRESTService.post(NOVA_VOLUME, "types", [volume_type: [name:volumeType1.name]]).returns([volume_type: volumeType1]).stub()
        play {
            assertEquals([volume_type: volumeType1], service.createVolumeType(volumeType1.name))
        }
    }

    def testGetVolumeTypeById() {
        service.openStackRESTService.get(NOVA_VOLUME, "types/$volumeType1.id").returns([volume_type: volumeType1]).stub()
        play {
            assertEquals(volumeType1, service.getVolumeTypeById(volumeType1.id))
        }
    }

    def testDeleteVolumeTypeById() {
        service.openStackRESTService.delete(NOVA_VOLUME, "types/$volumeType1.id").returns(null).stub()
        play {
            assertNull(service.deleteVolumeTypeById(volumeType1.id))
        }

    }

    void testExists() {
        service.openStackRESTService.get(NOVA_VOLUME, "volumes/detail").returns([volumes: [volume1, volume2]]).stub()

        play {
            assertTrue(service.exists(volume1.display_name))
            assertFalse(service.exists('123'))
        }
    }

    void testGetAvailablePlaceInQuotas() {
        service.openStackRESTService.get(NOVA_VOLUME, "volumes/detail").returns([volumes: [volume1, volume2]]).stub()
        Quota quota1 = new Quota()
        Quota quota2 = new Quota()
        quota1.limit = '5'
        quota2.limit = '7'

        service.quotaService.getQuotaByName(QuotaService.GIGABYTES).returns(quota1).times(1)
        service.quotaService.getQuotaByName(QuotaService.VOLUMES).returns(quota2).times(1)

        def resp = [:]
        resp[QuotaService.GIGABYTES] = 3
        resp[QuotaService.VOLUMES] = 5

        play {
            assertEquals(resp, service.getAvailablePlaceInQuotas())
        }
    }
}
