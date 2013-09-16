package asgard

import com.paypal.asgard.InfoService
import grails.test.mixin.TestFor
import org.gmock.GMockTestCase
import org.gmock.WithGMock
import org.junit.Before

@WithGMock
@TestFor(InfoService)
class InfoServiceTest extends GMockTestCase {

    private static final Date buildDate = new Date(System.currentTimeMillis())
    private static final int buildNumber = 200

    @Before
    void setUp() {
        service.buildNumber = buildNumber
        service.buildDate = buildDate
    }

    def testGetInfo() {
        def response = ['Build Number' : buildNumber, 'Build Date' : buildDate]
        play {
            assertEquals(response, service.getInfo())
        }
    }
}

