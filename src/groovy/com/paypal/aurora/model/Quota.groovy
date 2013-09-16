package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Quota {
    String name
    String limit
    String displayName

    Quota() {
    }

    Quota(def data) {
        name = data.key
        limit = data.value
        displayName = formatName(name)
    }

    def addDisplayName() {
        this.displayName = formatName(this.name)
    }

    private static String formatName(String name) {
        String[] words = name.split('_')
        StringBuilder result = new StringBuilder()

        for (String word in words) {
            if (words.length > 0) {
                result.append(" ")
            }
            result.append(word.substring(0, 1).toUpperCase())
            result.append(word.substring(1, word.size()).toLowerCase())
        }

        return result.toString()
    }


    @Override
    public String toString() {
        return "Quota{" +
                "name='" + name + '\'' +
                ", limit='" + limit + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
