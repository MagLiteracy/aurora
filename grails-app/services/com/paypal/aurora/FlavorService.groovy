package com.paypal.aurora

import com.paypal.aurora.model.Flavor

/**
 * Scrapes web pages and json feeds from Amazon to parse technical and financial information about instance types.
 */
class FlavorService {

    static transactional = false

    def openStackRESTService

    def listAll() {
        def resp = openStackRESTService.get(openStackRESTService.NOVA, 'flavors/detail')
        List<Flavor> result = [] as LinkedList
        for (flavor in resp.flavors) {
            result.push(new Flavor(flavor))
        }
        return result
    }

    def delete(String id) {
        openStackRESTService.delete(openStackRESTService.NOVA, "flavors/$id")
    }

    def create(Map flavor) {
        flavor.with {
            if (!containsKey('ephemeral') || ephemeral == '') ephemeral = 0
            if (!containsKey('swap') || swap == '') swap = 0
            if (!containsKey('rxtxFactor') || rxtxFactor == '') rxtxFactor = 1
        }
        def body = [flavor: [
                name: flavor.name,
                ram: Integer.valueOf(flavor.ram),
                disk: Integer.valueOf(flavor.disk),
                vcpus: Integer.valueOf(flavor.vcpus),
                'os-flavor-access:is_public': flavor.isPublic == 'on',
                'OS-FLV-EXT-DATA:ephemeral': Integer.valueOf(flavor.ephemeral),
                swap: Integer.valueOf(flavor.swap),
                rxtx_factor: Double.valueOf(flavor.rxtxFactor),
                id: null]
        ]
        openStackRESTService.post(openStackRESTService.NOVA, 'flavors', body)
    }

    Flavor getById(String id) {
        def resp = openStackRESTService.get(openStackRESTService.NOVA, "flavors/$id")
        return new Flavor(resp.flavor)
    }
}

