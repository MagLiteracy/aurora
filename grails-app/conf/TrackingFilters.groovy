import com.paypal.aurora.Requests
import org.joda.time.DateTime

import java.util.regex.Pattern

class TrackingFilters {

    static final Pattern nonBrowserUserAgents = Pattern.compile(
            '.*(libcurl|Python-urllib|Wget|HttpClient|lwp-request|Java).*')

    static filters = {
        all(controller: '*', action: '*') {
            before = {
                Requests.preventCaching(response)

                String userAgent = request.getHeader('user-agent')
                if (userAgent?.contains('MSIE') && !userAgent.contains('chromeframe')) {
                    request['ieWithoutChromeFrame'] = true
                }

                if (!request["originalRequestDump"]) {
                    request["originalRequestDump"] = Requests.stringValue(request)
                }
                if (session.isNew()) {
                    String hostName = Requests.getClientHostName(request)
                    if (userAgent && !userAgent.matches(nonBrowserUserAgents)) {
                        log.info "${new DateTime()} Session started. Client ${hostName}, User-Agent ${userAgent}"
                    }
                }

                // If the last value is falsy and there is no explicit return statement then this filter method will
                // return a falsy value and cause requests to fail silently.
                return true
            }
        }
    }
}
