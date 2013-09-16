package com.paypal.asgard.exception;

public class LbaasException extends RuntimeException {

    LbaasException() {
        super();
    }

    LbaasException(String message) {
        super(message);
    }

    LbaasException(String message, Throwable cause) {
        super(message, cause);
    }

    LbaasException(Throwable cause) {
        super(cause);
    }
}
