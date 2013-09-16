package com.paypal.asgard.model

class UserState {
    def dataCenterName
    def tenantId

    def UserState() {

    }

    def UserState(def dataCenterName, def tenantId) {
        this.dataCenterName = dataCenterName
        this.tenantId = tenantId
    }



    @Override
    public String toString() {
        return "UserState{" +
                "dataCenterName=" + dataCenterName +
                ", tenantId=" + tenantId +
                '}';
    }
}
