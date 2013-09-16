package com.paypal.aurora

import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.config.BeanPostProcessor

class ServiceInitLoggingBeanPostProcessor implements BeanPostProcessor {

    Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (beanName =~ /Service$/) {
            LogFactory.getLog(bean.class).info "Initializing..."
        }
        bean
    }

    Object postProcessAfterInitialization(Object bean, String beanName) {
        bean
    }
}
