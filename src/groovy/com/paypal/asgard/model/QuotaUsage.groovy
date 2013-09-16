package com.paypal.asgard.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class QuotaUsage {
    String name
    String displayName
    Integer limit
    Integer usage
    Integer left


    QuotaUsage(String displayName, Integer limit, Integer usage) {
        this.displayName = displayName
        this.name = normalizeName(displayName)
        this.limit = limit
        this.usage = usage
    }

    QuotaUsage() {
    }

    String normalizeName(String displayName){
        displayName.toLowerCase().replaceAll("\\s","_").replaceAll("[\\W^-]","")
    }

    Integer getLeft() {
        int left = limit - usage
        return Math.max(0, left)
    }


    @Override
    public String toString() {
        return "QuotaUsage{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", limit=" + limit +
                ", usage=" + usage +
                ", left=" + left +
                '}';
    }
}
