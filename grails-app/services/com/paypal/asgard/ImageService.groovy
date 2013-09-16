package com.paypal.asgard

import com.paypal.asgard.model.Image
import com.paypal.asgard.model.SnapshotProperties
import org.apache.shiro.SecurityUtils

class ImageService {

    private static final String IMAGES = 'images'

    private static final String X_META_IMAGE = 'X-Image-Meta'

    private static final List RESTRICTED_IMAGE_TYPES = ['aki', 'ari'] //only admins have access to these types

    def openStackRESTService

    private def getAllImagesOrSnapshots(def type) {
        def resp = openStackRESTService.get(openStackRESTService.GLANCE, "${IMAGES}/detail")
        def images = []
        for (im in resp.images) {
            def image = new Image(im)
            def allowed = SecurityUtils.subject?.hasRole(Constant.ROLE_ADMIN) || !(image.diskFormat in RESTRICTED_IMAGE_TYPES)
            if (allowed && image.type == type) {
                images << image
            }
        }
        images.sort { it.name }
    }

    def getAllImages() {
        getAllImagesOrSnapshots("image")
    }

    def getAllInstanceSnapshots() {
        getAllImagesOrSnapshots("snapshot")
    }

    def getImageById(def imageId) {
        def headers = openStackRESTService.head(openStackRESTService.GLANCE, "${IMAGES}/${imageId}")
        createImageFromHeaders(headers)
    }

    def createImage(def params) {
        def tokens = ["${X_META_IMAGE}-name": params.name, "${X_META_IMAGE}-is_public": params.shared == 'on', "${X_META_IMAGE}-container_format": 'bare',"${X_META_IMAGE}-min_disk":params.minDisk,
                        "${X_META_IMAGE}-min_ram":params.minRam,"${X_META_IMAGE}-disk_format":params.diskFormat]

        def postResponse = openStackRESTService.post(openStackRESTService.GLANCE, "${IMAGES}", null, tokens)

        def putResponse
        if (postResponse){
            putResponse = openStackRESTService.put(openStackRESTService.GLANCE, "${IMAGES}/${postResponse.image.id}", ['x-glance-api-copy-from': params.location])
        }
        new Image(putResponse.image)
    }

    def updateImage(def params) {
        def tokens = ["${X_META_IMAGE}-name": params.name, "${X_META_IMAGE}-is_public": params.shared == 'on', 'x-glance-registry-purge-props': false]
        openStackRESTService.put(openStackRESTService.GLANCE, "${IMAGES}/${params.id}", tokens)
    }

    def deleteImageById(def imageId) {
        openStackRESTService.delete(openStackRESTService.NOVA, "${IMAGES}/${imageId}")
    }

    boolean exists(String imageName) {
        for (Image image : allImages) {
            if(image.name.equals(imageName)) {
                return true
            }
        }
        return false
    }

    private static def createImageFromHeaders(def headers) {
        Image image = new Image()
        SnapshotProperties snapshotProperties = new SnapshotProperties()

        def values = headers*.buffer*.toString()
        def cutValue = { String value -> value.substring(value.indexOf(':') + 1).trim() }

        values.each() { it ->
            if (it.contains(X_META_IMAGE)) {
                def someValue = cutValue(it)
                if (!it.contains('Property')) {
                    if (it.contains('-Id')) {
                        image.id = someValue
                    }
                    if (it.contains('-Name')) {
                        image.name = someValue
                    }
                    if (it.contains('-Is_public')) {
                        image.shared = someValue.toLowerCase()
                    }
                    if (it.contains('-Status')) {
                        image.status = someValue
                    }
                    if (it.contains('-Min_disk')) {
                        image.minDisk = someValue
                    }
                    if (it.contains('-Min_ram')) {
                        image.minRam = someValue
                    }
                    if (it.contains('-Created_at')) {
                        image.created = someValue
                    }
                    if (it.contains('-Updated_at')) {
                        image.updated = someValue
                    }
                    if (it.contains('-Container_format')) {
                        image.containerFormat = someValue
                    }
                    if (it.contains('-Disk_format')) {
                        image.diskFormat = someValue
                    }
                    if (it.contains('-Checksum')) {
                        image.checksum = someValue
                    }
                }else{
                    if (it.contains('-User_id')) {
                        snapshotProperties.user_id = someValue
                    }
                    if (it.contains('-Image_state')) {
                        snapshotProperties.image_state = someValue
                    }
                    if (it.contains('-Image_type')) {
                        snapshotProperties.image_type = someValue
                    }
                    if (it.contains('-Image_location')) {
                        snapshotProperties.image_location = someValue
                    }
                    if (it.contains('-Instance_uuid')) {
                        snapshotProperties.instance_uuid = someValue
                    }
                    if (it.contains('-Base_image_ref')) {
                        snapshotProperties.base_image_ref = someValue
                    }
                    if (it.contains('-Owner_id')) {
                        snapshotProperties.owner_id = someValue
                    }
                }
            }
        }
        if (snapshotProperties.image_type == "snapshot"){
            image.type = snapshotProperties.image_type
            image.properties = snapshotProperties
        }
        image
    }

}
