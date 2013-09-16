package com.paypal.aurora.listeners

import com.paypal.aurora.PservService
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class PservListener implements ServiceListener {

    private static final logger = LogFactory.getLog(this)

    def action
    def ticketId
    def description

    @Autowired
    PservService pservService

    public PservListener() {

    }

    public PservListener(String action, String description) {
        this.action = action
        this.description = description
    }

    @Override
    void beforeInvoke(EventBefore event) {
        if(!pservService.isEnabled()){
            logger.info('PServ service disabled')
            return
        }
        ticketId = pservService.createTicket(action, description)
        pservService.addNote(ticketId, "\nBefore action: ${action} \nParams: $event.arguments")
    }

    @Override
    void afterInvoke(EventAfter event) {
        if(!pservService.isEnabled()){
            logger.info('PServ service disabled')
            return
        }
        pservService.addNote(ticketId, """
After action: $action
Result: $event.result
""")
        pservService.closeTicket(ticketId)
    }

    @Override
    void onException(EventAfter event) {
        if(!pservService.isEnabled()){
            logger.info('PServ service disabled')
            return
        }
        pservService.addNote(ticketId, """
After action: $action
Exception:
Cause: ${ExceptionUtils.getRootCauseMessage(event.exception)}
Stacktrace: ${ExceptionUtils.getStackTrace(event.exception)}
""")
        pservService.closeTicket(ticketId)
    }
}
