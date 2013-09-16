package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode


@EqualsAndHashCode
class SnapshotProperties {
    String ramdisk_id
    String image_location
    String image_type
    String image_state
    String kernel_id
    String owner_id
    String user_id
    String instance_uuid
    String base_image_ref

    SnapshotProperties() {

    }

    SnapshotProperties(Map properties) {
        this.ramdisk_id = properties.ramdisk_id
        this.image_location = properties.image_location
        this.image_type = properties.image_type
        this.image_state = properties.image_state
        this.kernel_id = properties.kernel_id
        this.owner_id = properties.owner_id
        this.user_id = properties.user_id
        this.instance_uuid = properties.instance_uuid
        this.base_image_ref = properties.base_image_ref
    }

    @Override
    public String toString() {
        return "SnapshotProperties{" +
                "ramdisk_id='" + ramdisk_id + '\'' +
                ", image_location='" + image_location + '\'' +
                ", image_type='" + image_type + '\'' +
                ", image_state='" + image_state + '\'' +
                ", kernel_id='" + kernel_id + '\'' +
                ", owner_id='" + owner_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", instance_uuid='" + instance_uuid + '\'' +
                ", base_image_ref='" + base_image_ref + '\'' +
                '}';
    }
}
