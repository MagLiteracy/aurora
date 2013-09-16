package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Snapshot {
    String id
    String name
    String created
    String description
    String size
    String status
    String volumeId

    Snapshot() {

    }

    Snapshot(Map data) {
        id = data.id
        name = data.display_name
        created = data.created_at
        description = data.display_description
        size = data.size
        status = data.status
        volumeId = data.volume_id
    }

    @Override
    public String toString() {
        return "Snapshot{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", created='" + created + '\'' +
                ", description='" + description + '\'' +
                ", size='" + size + '\'' +
                ", status='" + status + '\'' +
                ", volumeId='" + volumeId + '\'' +
                '}';
    }
}