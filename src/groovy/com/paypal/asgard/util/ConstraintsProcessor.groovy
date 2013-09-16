package com.paypal.asgard.util

abstract class ConstraintsProcessor {
    static def getConstraints(def command) {
        def requiredFields = [:]

        command?.constraints?.each {name, constraint ->
            if (isRequired(constraint)) {
                requiredFields[name] = 'required'
            }
        }

        requiredFields
    }

    static boolean isRequired(def constraint) {
        return (!constraint['blank'] && constraint['blank'] != null) ||
                (!constraint['nullable'] && constraint['nullable'] != null)
    }
}
