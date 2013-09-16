package com.paypal.asgard.exception;

public class RestClientRequestException extends RuntimeException {

    RestClientRequestException() {
        super();
    }

    RestClientRequestException(String message) {
        super(message);
    }

    RestClientRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    RestClientRequestException(Throwable cause) {
        super(cause);
    }

}
