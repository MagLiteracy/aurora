package com.paypal.aurora

import com.paypal.aurora.model.SecurityGroup

class SecurityGroupService {

    private static final String OS_SECURITY_GROUPS = 'os-security-groups'

    private static final String OS_SECURITY_GROUP_RULES = 'os-security-group-rules'

    def openStackRESTService

    def getAllSecurityGroups() {
        def resp = openStackRESTService.get(openStackRESTService.NOVA, OS_SECURITY_GROUPS)
        def securityGroups = []
        for (sec_group in resp.security_groups) {
            securityGroups << new SecurityGroup(sec_group)
        }
        securityGroups.sort { it.id as String }
    }

    def getSecurityGroupById(def id) {
        def resp = openStackRESTService.get(openStackRESTService.NOVA, "${OS_SECURITY_GROUPS}/${id}")
        new SecurityGroup(resp.security_group)
    }

    def createSecurityGroup(def params) {
        def body = [security_group: [name: params.name, description: params.description]]
        def resp = openStackRESTService.post(openStackRESTService.NOVA, OS_SECURITY_GROUPS, body)
        new SecurityGroup(resp.security_group)
    }

    def createSecurityGroupRule(def params) {
        def body = [security_group_rule: [
                ip_protocol: params.ipProtocol.toLowerCase(),
                from_port: params.fromPort,
                to_port: params.toPort,
                parent_group_id: params.id]]

        if (params.sourceGroup == '0') {
            body.security_group_rule.cidr = params.cidr
            body.security_group_rule.group_id = null
        } else {
            body.security_group_rule.cidr = null
            body.security_group_rule.group_id = params.sourceGroup
        }

        openStackRESTService.post(openStackRESTService.NOVA, OS_SECURITY_GROUP_RULES, body)
    }

    def deleteSecurityGroupRuleById(def id) {
        openStackRESTService.delete(openStackRESTService.NOVA, "${OS_SECURITY_GROUP_RULES}/${id}")
    }

    def deleteSecurityGroupById(def id) {
        openStackRESTService.delete(openStackRESTService.NOVA, "${OS_SECURITY_GROUPS}/${id}")
    }

}