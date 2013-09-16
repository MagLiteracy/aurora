package com.paypal.aurora.auth

import org.apache.shiro.authc.UsernamePasswordToken


class UserLoginToken extends UsernamePasswordToken {

    String environment

    UserLoginToken() {

    }

    UserLoginToken(final String username, final String password, final String environment) {
        super(username, password, null);
        this.environment = environment
    }



    @Override
    public String toString() {
        return "UserLoginToken{" +
                "username='" + username + '\'' +
                "environment='" + environment + '\'' +
                '}';
    }
}