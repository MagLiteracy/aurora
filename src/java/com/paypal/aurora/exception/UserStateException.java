package com.paypal.aurora.exception;

public class UserStateException extends RuntimeException {
    
    UserStateException() {
        super();
    }

    UserStateException(String message) {
        super(message);
    }

    UserStateException(String message, Throwable cause) {
        super(message, cause);
    }

    UserStateException(Throwable cause) {
        super(cause);
    }
}
