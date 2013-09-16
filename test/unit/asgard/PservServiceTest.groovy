package asgard

import com.paypal.asgard.OpenStackRESTService
import com.paypal.asgard.PservService
import com.paypal.asgard.SessionStorageService
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(PservService)
class PservServiceTest extends GMockTestCase {

    private static final String PSERV = 'pserv'
    private static final String TICKETS = 'tracking-rest-service/rest/tickets'
    private static final getTicketResponse = [
        application: "traffic-management",
        title: "Title of ticket",
        description: "Description for ticket",
        environment: "Development",
        ticketNumber: "ticketId1",
        dataCenterName: "SLC-A",
        dueDate: "2012/12/10",
        status: "IN_PROGRESS",
        requestor: [
            login: "abc",
            email: "abc@paypal.com",
            phone: "000-000-0000",
            location: "SJN"
        ]
    ]

    @Before
    void setUp() {
        OpenStackRESTService openStackRESTService = mock(OpenStackRESTService);
        service.openStackRESTService = openStackRESTService
        service.openStackRESTService.PSERV.returns(PSERV).stub()
        service.sessionStorageService = mock(SessionStorageService)
    }

    def testCreateTicket() {
        def user = 'user'
        def body = [application: [type:'ASGARD', subType:'LB Management'], title: 'action', description: 'desc', environment: 'name', dataCenterName: 'dataCenterName', assignee:'asgardmanager', status: 'IN_PROGRESS', requester: [login: user], submitter: user]
        service.openStackRESTService.post(PSERV, TICKETS, body, null, null, [header : 'application/json', body: 'text/plain']).returns(new StringReader('ticketId')).times(1)
        service.sessionStorageService.getCurrentEnv().returns([name: body.environment]).stub()
        service.sessionStorageService.getDataCenterName().returns(body.dataCenterName).stub()
        service.sessionStorageService.getUser().returns(user).stub()

        play {
            assertEquals('ticketId', service.createTicket(body.title, body.description))
        }

    }

    def testGetTicket() {
        service.openStackRESTService.get(PSERV, "${TICKETS}/ticketId").returns(getTicketResponse).times(1)

        play {
            assertEquals(getTicketResponse, service.getTicket('ticketId'))
        }
    }

    def testAddNote() {
        service.openStackRESTService.post(PSERV, "${TICKETS}/ticketId/note", 'note1', null, 'text/plain').returns(new StringReader('')).times(1)

        play {
            assertEquals('', service.addNote('ticketId', 'note1').str.toString())
        }
    }

    def testCloseTicket() {
        service.openStackRESTService.put(PSERV, (String)"${TICKETS}/ticketId/status", null, '\"COMPLETED\"').returns(new StringReader('')).times(1)

        play {
            assertEquals('', service.closeTicket('ticketId').str.toString())
        }
    }

    def testIsServiceEnabled() {
        service.openStackRESTService.isServiceEnabled(PSERV).returns(true).times(1)

        play {
            assertTrue(service.isEnabled())
        }
    }
}
