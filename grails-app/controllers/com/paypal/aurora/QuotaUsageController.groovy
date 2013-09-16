package com.paypal.aurora

import com.paypal.aurora.model.QuotaUsage
import grails.converters.JSON
import grails.converters.XML

class QuotaUsageController {

    def quotaService
    def sessionStorageService

    def index() { redirect(action: 'list', params: params)}

    def list = {
        List<QuotaUsage> quotaUsages = quotaService.getQuotaUsage(sessionStorageService.tenant.id)
        def model = [quotaUsages : quotaUsages]
        withFormat {
            html { [quotaUsages: quotaUsages] }
            xml { new XML(model).render(response) }
            json { new JSON(model).render(response) }
        }
    }
}
