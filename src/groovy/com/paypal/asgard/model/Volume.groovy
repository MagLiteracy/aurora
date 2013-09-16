package com.paypal.asgard.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Volume {
    String id
    String displayName
    String description
    String status
    String volumeType
    String size
    String instanceId
    String instanceName
    String device

    Volume() {

    }

    Volume(Map data) {
        id = data.id
        displayName = data.display_name
        description = data.display_description
        status = data.status
        volumeType = data.volume_type
        size = data.size
        instanceId = data.attachments?.server_id[0]
        device = data.attachments?.device[0]
    }


    @Override
    public String toString() {
        return "Volume{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", volumeType='" + volumeType + '\'' +
                ", size='" + size + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", instanceName='" + instanceName + '\'' +
                ", device='" + device + '\'' +
                '}';
    }
}
