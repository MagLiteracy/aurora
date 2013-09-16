package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Keypair {
    String name
    String publicKey
    String privateKey
    String fingerprint
    String userId

    Keypair() {
    }

    Keypair(def keypair) {
        this.name = keypair.name
        this.publicKey = keypair.public_key
        this.fingerprint = keypair.fingerprint
        this.privateKey = keypair.private_key
        this.userId = keypair.user_id
    }


    @Override
    public String toString() {
        return "Keypair{" +
                "name='" + name + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", fingerprint='" + fingerprint + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
