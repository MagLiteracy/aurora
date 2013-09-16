package com.paypal.asgard.model

class VolumeType {
    String id
    String name

    VolumeType() {}

    VolumeType(def type) {
        id = type.id
        name = type.id
    }

    @Override
    public String toString() {
        return "VolumeType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
