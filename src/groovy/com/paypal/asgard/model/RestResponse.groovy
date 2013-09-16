package com.paypal.asgard.model

import groovy.transform.Canonical

@Canonical
class RestResponse {
    final int statusCode
    final String content
}
