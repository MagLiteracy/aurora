package com.paypal.aurora

class ValidatorUtils {

    def static checkInteger(String value) {
        if(value != null && !value.isInteger()) {
            return 'typeMismatch.java.lang.Integer'
        }
    }

    def static checkDouble(String value) {
        if(value != null && !value.isInteger()) {
            return 'typeMismatch.java.lang.Double'
        }
    }
}
