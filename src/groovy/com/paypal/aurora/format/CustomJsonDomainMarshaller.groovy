package com.paypal.aurora.format

import grails.converters.JSON
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import org.codehaus.groovy.grails.web.json.JSONWriter
import org.springframework.beans.BeanUtils

/**
 * Custom JSON marshaller. Used for classes from packages starting with PACKAGE_PREFIX.
 * Does not marshal properties with names in EXCLUDED list
 */
public class CustomJsonDomainMarshaller implements ObjectMarshaller<JSON> {

    private static final EXCLUDED = ['class','metaClass','version']

    private static final PACKAGE_PREFIX = "com.paypal"

    public boolean supports(Object object) {
        return object?.class?.package?.name?.startsWith(PACKAGE_PREFIX)
    }

    public void marshalObject(Object o, JSON json) throws ConverterException {
        JSONWriter writer = json.getWriter();
        try {
            writer.object();
            def properties = BeanUtils.getPropertyDescriptors(o.getClass());
            for (property in properties) {
                String name = property.getName();
                if(!EXCLUDED.contains(name)) {
                    def readMethod = property.getReadMethod();
                    if (readMethod != null) {
                        def value = readMethod.invoke(o, (Object[]) null);
                        writer.key(name);
                        json.convertAnother(value);
                    }
                }
            }
            writer.endObject();
        } catch (Exception e) {
            throw new ConverterException("Exception in CustomJsonDomainMarshaller", e);
        }
    }
}