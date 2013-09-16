package com.paypal.aurora.listeners

public interface ServiceListener {
    void beforeInvoke(EventBefore event);
    void afterInvoke(EventAfter event);
    void onException(EventAfter event);
}