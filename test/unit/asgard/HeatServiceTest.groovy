package asgard

import com.paypal.asgard.HeatService
import com.paypal.asgard.OpenStackRESTService
import com.paypal.asgard.SessionStorageService
import com.paypal.asgard.model.RestResponse
import com.paypal.asgard.model.Stack
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(HeatService)
class HeatServiceTest extends GMockTestCase {

    static final String HEAT = 'orchestration'

    static final stack1 = [id: 'id1', stack_name: 'name1']
    static final stack2 = [id: 'id2', stack_name: 'name2']

    static final Integer templateInd = 345
    static final stackName = "newName"
    static final template = '{JSON template description}'
    static final createParams = [DBPassword: 'pwd', DBRootPassword: 'pws', DBUsername: 'user', LinuxDistribution: 'distro', DBName: 'db', KeyName: 'key', InstanceType: 't1.micro']

    static final RestResponse CreatedResponse = new RestResponse(201, 'Created')

    static final json = '{"Description" : "A Database instance running a local MySQL server",\n' +
            '  "Parameters" : {\n' +
            '    "ParamName1" : {\n' +
            '      "AllowedValues" : [ "blah1", "blah2"],\n' +
            '      "OtherStuff" : "ignored"\n' +
            '    },\n' +
            '\n' +
            '    "ParamName2": {\n' +
            '      "AllowedValues" : [ "allowed" ],\n' +
            '      "Default": "default"\n' +
            '    }\n' +
            '  }}'

    static final json1 = '{"Description" : "A Database instance running a local MySQL server","Parameters" : {}}'

    static final parsedParams = [[name: 'ParamName2', default: 'default', allowedValues: ["allowed"]], [name: 'ParamName1', default: null, allowedValues: ["blah1", "blah2"]]]


    @Before
    void setUp() {
        OpenStackRESTService openStackRESTService = mock(OpenStackRESTService);
        service.openStackRESTService = openStackRESTService
        service.openStackRESTService.HEAT.returns(HEAT).stub()

        SessionStorageService sessionStorageService = mock(SessionStorageService)
        service.sessionStorageService = sessionStorageService
    }

    def testListAll() {
        service.openStackRESTService.get(HEAT, 'stacks').returns([stacks: [stack1, stack2]]).stub()
        play {
            assertEquals([new Stack(stack1), new Stack(stack2)], service.listAll())
        }
    }

    def testGetById() {
        service.openStackRESTService.get(HEAT, "stacks/$stack1.id").returns([stack: stack1]).stub()
        play {
            assertEquals(new Stack(stack1), service.getById(stack1.id))
        }

    }

    def testDelete() {
        service.openStackRESTService.get(HEAT, "stacks/$stack1.id").returns([stack: stack1]).stub()
        service.openStackRESTService.delete(HEAT, "stacks/$stack1.stack_name/$stack1.id").returns(null).stub()

        play {
            assertNull(service.delete(stack1.id))
        }
    }

    def testCreateStack() {
        service.openStackRESTService.post(HEAT, 'stacks', [stack_name: stackName,
                template: template,
                timeout_mins: 60,
                parameters: createParams]).returns(CreatedResponse).stub()

        service.sessionStorageService.getExpiringVar(templateInd).returns(template).stub()

        play {
            assertEquals(CreatedResponse, service.createStack(templateInd, stackName, createParams))
        }
    }

    def testParseParams() {
        assertEquals(parsedParams, service.parseParams(json))
        assertEquals([], service.parseParams(json1))
    }

}
