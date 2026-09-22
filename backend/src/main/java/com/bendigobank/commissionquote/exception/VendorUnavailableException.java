package com.bendigobank.commissionquote.exception;

// Covers timeouts, connection failures, and non-2xx vendor responses - mapped to 503.
public class VendorUnavailableException extends RuntimeException {

    public VendorUnavailableException(String message) {
        super(message);
    }

    public VendorUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
