package com.paypal.aurora.model

import groovy.transform.Canonical

@Canonical
class RestResponse {
    final int statusCode
    final String content
}
