package com.paypal.asgard

import com.paypal.asgard.auth.UserLoginToken
import org.apache.shiro.authc.SimpleAuthenticationInfo

class UserLoginTokenRealm {

    static authTokenClass = UserLoginToken

    def openStackRESTService

    def sessionStorageService

    def authenticate(UserLoginToken userLoginToken) {
        openStackRESTService.login(userLoginToken)
        new SimpleAuthenticationInfo(userLoginToken.principal, userLoginToken.credentials, UserLoginTokenRealm.class.getSimpleName())
    }

    def hasRole(principal, roleName) {
        sessionStorageService.roles.contains(roleName)
    }

    //need to implement
    def isPermitted(principal, requiredPermission) {
        true
    }

}
