package com.paypal.asgard.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Image {
    String id
    String name
    String type = 'image'
    String shared
    String status
    String minDisk
    String minRam
    String created
    String updated
    String containerFormat
    String diskFormat
    String checksum
    SnapshotProperties properties

    Image() {

    }

    Image(Map data) {
        if (data.properties?.image_type){
            type = data.properties.image_type
            properties = new SnapshotProperties(data.properties)
        }
        id = data.id
        name = data.name
        shared = data.is_public
        diskFormat = data.disk_format
        status = data.status
        created = data.created_at
        updated = data.updated_at
        minDisk = data.min_disk
        minRam = data.min_ram
        containerFormat = data.container_format
        checksum = data.checksum
    }


    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", shared='" + shared + '\'' +
                ", status='" + status + '\'' +
                ", minDisk='" + minDisk + '\'' +
                ", minRam='" + minRam + '\'' +
                ", created='" + created + '\'' +
                ", updated='" + updated + '\'' +
                ", containerFormat='" + containerFormat + '\'' +
                ", diskFormat='" + diskFormat + '\'' +
                ", checksum='" + checksum + '\'' +
                ", properties=" + properties +
                '}';
    }
}

