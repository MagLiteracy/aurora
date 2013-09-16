package com.paypal.asgard.model

class Instance {

    private final static def POWER_STATE = [(0): "Pending", (1): "Running", (3): "Paused",
            (4): "Shutdown", (6): "Crashed", (7): "Suspended", (9): "Building"]

    String name
    String status
    String taskStatus
    String powerStatus
    String instanceId
    String flavorId
    List<IPContainer> networks = []
    List<String> securityGroups
    String keyName
    String imageId
    String host
    List<IPContainer> floatingIps = []
    /**
     * Used for list instances. When external floating IP is enabled.
     */
    String displayedIp

    Instance() {
    }

    Instance(Map data) {
        instanceId = data.id
        name = data.name
        status = data.status.toLowerCase().capitalize()
        flavorId = data.flavor.id
        data.addresses.each() { key, value ->
            if (value[0]?.addr) {
                networks << new IPContainer(value[0].addr, key)
            }
        }
        securityGroups = []
        for (group in data.security_groups) {
            securityGroups.add(group.name)
        }
        keyName = data.key_name
        imageId = data.image.id
        host = data.get("OS-EXT-SRV-ATTR:host")
        taskStatus = data.get("OS-EXT-STS:task_state")?.capitalize()
        powerStatus = POWER_STATE.get(data.get("OS-EXT-STS:power_state"))
    }


    @Override
    public String toString() {
        return "Instance{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", flavorId='" + flavorId + '\'' +
                ", networks=" + networks +
                ", securityGroups=" + securityGroups +
                ", keyName='" + keyName + '\'' +
                ", imageId='" + imageId + '\'' +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Instance instance = (Instance) o

        if (flavorId != instance.flavorId) return false
        if (host != instance.host) return false
        if (imageId != instance.imageId) return false
        if (instanceId != instance.instanceId) return false
        if (keyName != instance.keyName) return false
        if (name != instance.name) return false
        if (networks != instance.networks) return false
        if (powerStatus != instance.powerStatus) return false
        if (securityGroups != instance.securityGroups) return false
        if (status != instance.status) return false
        if (taskStatus != instance.taskStatus) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = 31 * result + (status != null ? status.hashCode() : 0)
        result = 31 * result + (taskStatus != null ? taskStatus.hashCode() : 0)
        result = 31 * result + (powerStatus != null ? powerStatus.hashCode() : 0)
        result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0)
        result = 31 * result + (flavorId != null ? flavorId.hashCode() : 0)
        result = 31 * result + (networks != null ? networks.hashCode() : 0)
        result = 31 * result + (securityGroups != null ? securityGroups.hashCode() : 0)
        result = 31 * result + (keyName != null ? keyName.hashCode() : 0)
        result = 31 * result + (imageId != null ? imageId.hashCode() : 0)
        result = 31 * result + (host != null ? host.hashCode() : 0)
        return result
    }
}
