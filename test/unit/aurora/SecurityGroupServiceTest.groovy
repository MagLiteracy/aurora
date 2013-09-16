package aurora

import com.paypal.aurora.OpenStackRESTService
import com.paypal.aurora.SecurityGroupService
import com.paypal.aurora.model.SecurityGroup
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(SecurityGroupService)
class SecurityGroupServiceTest extends GMockTestCase {


    static final NOVA = 'compute'
    static final OS_SECURITY_GROUPS = 'os-security-groups'
    static final OS_SECURITY_GROUP_RULES = 'os-security-group-rules'

    static final rule1 = [id: 21, to_port: 80, ip_protocol: 'tcp', from_port: 80, ip_range: [cidr: '0.0.0.0/0'], parent_group_id: 1, group: [:]]
    static final rule2 = [id: 22, to_port: 8080, ip_protocol: 'tcp', from_port: 8080, ip_range: [cidr: '0.0.0.0/0'], parent_group_id: 1, group: [name: 'name2', tenant_id: 'tenantId2']]

    static final securityGroup1 = [id: 1, name: 'name1', description: 'description1', tenant_id: 'tenantId1', rules: [rule1, rule2]]
    static final securityGroup2 = [id: 2, name: 'name2', description: 'description2', tenant_id: 'tenantId2', rules: []]

    @Before
    void setUp() {
        OpenStackRESTService openStackRESTService = mock(OpenStackRESTService);
        service.openStackRESTService = openStackRESTService
        service.openStackRESTService.NOVA.returns(NOVA).stub()
    }

    def testGetAllSecurityGroups() {
        service.openStackRESTService.get(NOVA, OS_SECURITY_GROUPS).returns([security_groups: [securityGroup2, securityGroup1]]).stub()
        play {
            assertEquals([new SecurityGroup(securityGroup1), new SecurityGroup(securityGroup2)], service.getAllSecurityGroups())
        }
    }

    def testGetSecurityGroupById() {
        service.openStackRESTService.get(NOVA, OS_SECURITY_GROUPS + "/${securityGroup1.id}").returns([security_group: securityGroup1]).stub()
        play {
            assertEquals(new SecurityGroup(securityGroup1), service.getSecurityGroupById(securityGroup1.id))
        }
    }

    def testCreateSecurityGroup() {
        def body = [security_group: [name: securityGroup2.name, description: securityGroup2.description]]
        service.openStackRESTService.post(NOVA, OS_SECURITY_GROUPS, body).returns([security_group: securityGroup2]).stub()

        play {
            assertEquals(new SecurityGroup(securityGroup2), service.createSecurityGroup([name: securityGroup2.name, description: securityGroup2.description]))
        }
    }

    def testCreateSecurityGroupRule() {
        def body1 = [security_group_rule: [
                ip_protocol: rule1.ip_protocol,
                from_port: rule1.from_port,
                to_port: rule1.to_port,
                parent_group_id: rule1.parent_group_id,
                cidr: '0.0.0.0/0',
                group_id: null
        ]]

        service.openStackRESTService.post(NOVA, OS_SECURITY_GROUP_RULES, body1).returns([security_group_rule: rule1]).stub()

        def body2 = [security_group_rule: [
                ip_protocol: rule2.ip_protocol,
                from_port: rule2.from_port,
                to_port: rule2.to_port,
                parent_group_id: rule2.parent_group_id,
                group_id: securityGroup2.id,
                cidr: null
        ]]

        service.openStackRESTService.post(NOVA, OS_SECURITY_GROUP_RULES, body2).returns([security_group_rule: rule2]).stub()

        play {
            assertEquals([security_group_rule: rule1], service.createSecurityGroupRule([ipProtocol: rule1.ip_protocol, fromPort: rule1.from_port, toPort: rule1.to_port, id: rule1.parent_group_id, cidr: rule1.ip_range.cidr, sourceGroup: '0']))
            assertEquals([security_group_rule: rule2], service.createSecurityGroupRule([ipProtocol: rule2.ip_protocol, fromPort: rule2.from_port, toPort: rule2.to_port, id: rule1.parent_group_id, sourceGroup: 2]))
        }

    }

    def testDeleteSecurityGroupById() {
        service.openStackRESTService.delete(NOVA, OS_SECURITY_GROUPS + "/$securityGroup1.id").returns(null).stub()
        play {
            assertNull(service.deleteSecurityGroupById(securityGroup1.id))
        }
    }

    def testDeleteSecurityGroupRuleById() {
        service.openStackRESTService.delete(NOVA, OS_SECURITY_GROUP_RULES+ "/id1").returns(null).stub()
        play {
            assertNull(service.deleteSecurityGroupRuleById('id1'))
        }
    }


}
