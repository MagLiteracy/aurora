package aurora

import com.paypal.aurora.OpenStackRESTService
import com.paypal.aurora.OpenStackUserService
import com.paypal.aurora.model.OpenStackUser
import com.paypal.aurora.model.Role
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(OpenStackUserService)
class OpenStackUserServiceTest extends GMockTestCase {

    static final KEYSTONE = 'identity'

    static final USERS = 'users'
    static final TENANTS = 'tenants'
    static final ROLES = 'OS-KSADM/roles'
    static final TENANT_ID = 'tenantId'

    static final user1 =[id: 'userId1', name: 'name1', email: 'email1@com', enabled: true, tenant_id: 'tenantId1']
    static final user2 =[id: 'userId2', name: 'name2', email: 'email2@com', enabled: true, tenant_id: 'tenantId2']

    static final role1 = [id: 'roleId1', name: 'role1']
    static final role2 = [id: 'roleId2', name: 'role2']

    private static final String WRONG_USER_NAME = "123"
    @Before
    void setUp() {
        OpenStackRESTService openStackRESTService = mock(OpenStackRESTService);
        service.openStackRESTService = openStackRESTService
        service.openStackRESTService.KEYSTONE.returns(KEYSTONE).stub()
    }

    def testGetAllUsers() {
        service.openStackRESTService.get(KEYSTONE, USERS).returns([users: [user1, user2]]).stub()
        play {
            assertEquals([new OpenStackUser(user1), new OpenStackUser(user2)], service.getAllUsers())
        }
    }

    def testGetAllUsersByTenant() {
        service.openStackRESTService.get(KEYSTONE, "$TENANTS/$user1.tenant_id/$USERS").returns([users: [user1]]).times(1)
        play {
            assertEquals([new OpenStackUser(user1)], service.getAllUsersByTenant(user1.tenant_id))
        }
    }

    def testGetUserByName1() {
        service.openStackRESTService.get(KEYSTONE, USERS, [name: user1.name]).returns([user: user1]).times(1)
        play {
            assertEquals(new OpenStackUser(user1), service.getUserByName(user1.name))
        }
    }

    def testGetUserByName2() {
        service.openStackRESTService.get(KEYSTONE, USERS, [name: user1.name]).returns([users: [user1]]).times(1)
        play {
            assertEquals(new OpenStackUser(user1), service.getUserByName(user1.name))
        }
    }

    def testGetUserByNameWithWrongName() {
        service.openStackRESTService.get(KEYSTONE, USERS, [name: WRONG_USER_NAME]).returns([users: [user1]]).times(1)
        play {
            assertNull(service.getUserByName(WRONG_USER_NAME))
        }
    }

    def testGetUserById() {
        service.openStackRESTService.get(KEYSTONE, USERS + "/$user1.id").returns([user: user1]).stub()
        play {
            assertEquals(new OpenStackUser(user1), service.getUserById(user1.id))
        }
    }

    def testGetAllRoles() {
        service.openStackRESTService.get(KEYSTONE, ROLES).returns([roles: [role1, role2]]).stub()
        play {
            assertEquals([role1, role2], service.getAllRoles())
        }
    }

    def testCreateUser() {
        def body = [user :[ email : user1.email, password : 'pwd',
                name : user1.name, tenantId : user1.tenant_id, enabled : true]]

        service.openStackRESTService.post(KEYSTONE, USERS, body).returns([user: user1]).stub();

        def path = "$TENANTS/tenantId1/$USERS/userId1/roles/OS-KSADM/roleId1"

        service.openStackRESTService.put(KEYSTONE, path, null).returns([role: role1]).stub();

        play {
            assertEquals([role:role1], service.createUser([name: user1.name, email: user1.email, tenant_id: user1.tenant_id, role_id: 'roleId1', password: 'pwd']))
        }

    }

    def testUpdateUser() {
        def body = [user : [id : user1.id, name : user1.name, email : user1.email, password: 'pwd']]

        def putResponse = [user: [id: user1.id, name: user1.name, extra: [tenantId: user1.tenant_id, enabled: true, email: user1.email, password: 'encodedPassword']]]

        service.openStackRESTService.put(KEYSTONE, "$USERS/$user1.id", null, body).returns(putResponse).stub()
        service.openStackRESTService.put(KEYSTONE, "$USERS/$user1.id/", null, [user: [id: user1.id, tenantId: user1.tenant_id]]).returns(putResponse).stub()

        play {
            assertEquals(putResponse, service.updateUser([id: user1.id, name: user1.name, email: user1.email, password: 'pwd', tenant_id: 'tenantId1']))
        }
    }

    def testDeleteUserById() {
        service.openStackRESTService.delete(KEYSTONE, USERS + "/$user1.id").returns(null).stub()
        play {
            assertNull(service.deleteUserById(user1.id))
        }
    }

    def testGetUserRole() {
        service.openStackRESTService.get(KEYSTONE, "$TENANTS/tenantId1/$USERS/userId1/roles").returns([roles: [role1, role2]]).stub()
        play {
            assertEquals([role1, role2], service.getUserRole('userId1', 'tenantId1'))
        }
    }

    def testSetUserRole() {
        def path = "$TENANTS/tenantId1/$USERS/userId1/roles/OS-KSADM/roleId1"
        service.openStackRESTService.put(KEYSTONE, path, null).returns([role: role1]).stub();
        play {
            assertEquals([role: role1], service.setUserRole('tenantId1', 'userId1', 'roleId1'))
        }
    }

    def testDeleteUserRole() {
        def path = "$TENANTS/tenantId1/$USERS/userId1/roles/OS-KSADM/roleId1"
        service.openStackRESTService.delete(KEYSTONE, path).returns(null).stub();
        play {
            assertNull(service.deleteUserRole('tenantId1', 'userId1', 'roleId1'))
        }
    }

    def testGetUserRoles() {
        service.openStackRESTService.get(KEYSTONE, "$TENANTS/tenantId1/$USERS/userId1/roles").returns([roles: [role1, role2]]).stub()
        service.openStackRESTService.get(KEYSTONE, "$TENANTS/tenantId1/$USERS/userId2/roles").returns([roles: [role2, role1]]).stub()

        def users = [new OpenStackUser(user1), new OpenStackUser(user2)]

        play {
            assertEquals([(user1.id): new Role(role1), (user2.id): new Role(role2)], service.getUsersRoles(users, 'tenantId1'))
        }

    }

    def testRole() {

        def nullRole = new Role([id: null, name: null])
        def role = new Role([id: 'id1', name: 'role1'])

        assertEquals(nullRole, new Role(null))
        assertEquals(nullRole, new Role([:]))
        assertEquals(nullRole, new Role([]))
        assertEquals(nullRole, new Role([[:]]))
        assertEquals(role, new Role([[id: 'id1', name: 'role1'], [id: 'id2', name: 'role2']]))
        assertEquals(role, new Role([id: 'id1', name: 'role1']))
    }

    def testChangeUsersRole() {
        service.openStackRESTService.get(KEYSTONE, USERS, [name: user1.name]).returns(user: user1).times(1)
        service.openStackRESTService.get(KEYSTONE, USERS, [name: user2.name]).returns(user: user2).times(1)
        service.openStackRESTService.get(KEYSTONE, "$TENANTS/$TENANT_ID/$USERS/$user1.id/roles").returns([roles: [[id:role2.id, name:role2.name]]]).times(1)
        service.openStackRESTService.get(KEYSTONE, "$TENANTS/$TENANT_ID/$USERS/$user2.id/roles").returns([roles: [[id:role1.id, name:role1.name]]]).times(1)
        service.openStackRESTService.put(KEYSTONE, "$TENANTS/$TENANT_ID/$USERS/$user1.id/roles/OS-KSADM/${role1.id}", null).returns().times(1)
        service.openStackRESTService.put(KEYSTONE, "$TENANTS/$TENANT_ID/$USERS/$user2.id/roles/OS-KSADM/${role2.id}", null).returns().times(1)
        service.openStackRESTService.delete(KEYSTONE, "$TENANTS/$TENANT_ID/$USERS/$user1.id/roles/OS-KSADM/${role2.id}").returns().times(1)
        service.openStackRESTService.delete(KEYSTONE, "$TENANTS/$TENANT_ID/$USERS/$user2.id/roles/OS-KSADM/${role1.id}").returns().times(1)
        def usersRoles = [:]
        usersRoles[user1.name] = role1.id
        usersRoles[user2.name] = role2.id

        play {
            assertNull(service.changeUsersRole(usersRoles, TENANT_ID))
        }
    }
}
