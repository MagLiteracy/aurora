package com.paypal.aurora.format

import grails.converters.XML
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import org.springframework.beans.BeanUtils

/**
 * Custom XML marshaller. Used for classes from packages starting with PACKAGE_PREFIX.
 * Does not marshal properties with names in EXCLUDED list
 */
class CustomXmlDomainMarshaller implements ObjectMarshaller<XML> {
    private static final EXCLUDED = ['class','metaClass','version']

    private static final PACKAGE_PREFIX = "com.paypal"

    @Override
    public boolean supports(Object object) {
        return object?.class?.package?.name?.startsWith(PACKAGE_PREFIX)
    }

    @Override
    public void marshalObject(Object o, XML xml) throws ConverterException {
        def properties = BeanUtils.getPropertyDescriptors(o.class)
        for (property in properties) {
            String name = property.name
            if(!EXCLUDED.contains(name)) {
                def readMethod = property.readMethod
                if (readMethod != null) {
                    def value = readMethod.invoke(o, (Object[]) null)
                    xml.startNode(name)
                    xml.convertAnother(value)
                    xml.end()
                }
            }
        }
    }
}
