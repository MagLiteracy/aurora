import com.paypal.aurora.FastProperty
import com.paypal.aurora.format.CustomJsonDomainMarshaller
import com.paypal.aurora.format.CustomXmlDomainMarshaller
import com.paypal.aurora.listeners.EventAfter
import com.paypal.aurora.listeners.ServiceListener
import grails.converters.JSON
import grails.converters.XML

class BootStrap {

    def infoService
    def grailsApplication
    def restApiHandlerService
    def eventSubscribeService

    def init = { servletContext ->

        setupListenerEngine()

        infoService.buildDate = grailsApplication.metadata['app.build_date']?:"---"
        infoService.buildNumber = grailsApplication.metadata['app.build_number']?:"DEV"

        restApiHandlerService.initialize()

        eventSubscribeService.initialize()

        JSON.registerObjectMarshaller(FastProperty) {
            it.properties.subMap(FastProperty.ALL_ATTRIBUTES)
        }

        JSON.registerObjectMarshaller(new CustomJsonDomainMarshaller())

        XML.registerObjectMarshaller(new CustomXmlDomainMarshaller())
    }

    private void setupListenerEngine() {
        grailsApplication.serviceClasses.each { serviceClass ->
            serviceClass.metaClass.invokeMethod = { name, arguments ->
                if (name == 'hasProperty') {
                    return delegate.metaClass.getMetaMethod(name, arguments).doMethodInvoke(delegate, arguments)
                }
                EventAfter event = new EventAfter(
                    arguments: arguments,
                    delegate: delegate)
                Set<ServiceListener> listeners = new HashSet<>()
                if (delegate.hasProperty('listeners')) {
                    delegate.listeners.get(name)?.each { listeners << it.listener }
                }
                listeners.each { it.beforeInvoke(event) }
                try {
                    def result = delegate.metaClass.getMetaMethod(name, arguments).doMethodInvoke(delegate, arguments)
                    event.result = result
                    listeners.each { it.afterInvoke(event) }
                    return result
                } catch (e) {
                    event.exception = e
                    listeners.each { it.onException(event) }
                    throw e
                }
            }
        }
    }

}
