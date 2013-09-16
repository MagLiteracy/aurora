package com.paypal.asgard.exception;

public class QuantumDNSException extends RuntimeException {
    
    QuantumDNSException() {
        super();
    }

    QuantumDNSException(String message) {
        super(message);
    }

    QuantumDNSException(String message, Throwable cause) {
        super(message, cause);
    }

    QuantumDNSException(Throwable cause) {
        super(cause);
    }
}
