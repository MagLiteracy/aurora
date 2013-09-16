package com.paypal.asgard

import com.paypal.asgard.model.Keypair

class KeypairService {
    def openStackRESTService

    List<String> getAllNames() {
        def resp = openStackRESTService.get(openStackRESTService.NOVA, 'os-keypairs')
        List<String> result = [] as LinkedList
        for (keypair in resp.keypairs) {
            result.push(keypair.keypair.name)
        }
        return result
    }

    List<Keypair> listAll() {
        def resp = openStackRESTService.get(openStackRESTService.NOVA, 'os-keypairs')
        List<Keypair> result = [] as LinkedList
        for (keypair in resp.keypairs) {
            result.push(new Keypair(keypair.keypair))
        }
        return result
    }

    boolean exists(String keypairName) {
        List<Keypair> keypairs = listAll()
        for (Keypair keypair : keypairs) {
            if (keypair.name.equals(keypairName)) {
                return true;
            }
        }
        return false
    }

    Keypair getKeypairByName(String keypairName) {
        List<Keypair> keypairs = listAll()
        keypairs.find { it.name == keypairName }
    }

    Keypair create(String name) {
        def result
        if (exists(name)) {
            result = null
        } else {

            def body = [keypair: [
                    name: name]
            ]
            def resp = openStackRESTService.post(openStackRESTService.NOVA, 'os-keypairs', body)
            result = new Keypair(resp.keypair)
        }
        return result
    }

    Keypair insert(String name, String publicKey) {
        if (exists(name)) {
            return null
        }
        def body = [keypair: [
                name: name,
                public_key: publicKey]
        ]
        def resp = openStackRESTService.post(openStackRESTService.NOVA, 'os-keypairs', body)
        def result = new Keypair(resp.keypair)
        return result
    }

    def delete(String name) {
        openStackRESTService.delete(openStackRESTService.NOVA, 'os-keypairs/' + name)
    }


}

