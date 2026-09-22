package com.bendigobank.commissionquote.exception;

/**
 * Thrown whenever the vendor call fails for any reason that is not the
 * caller's fault: connection refused, read timeout, simulated random
 * outage, or a non-2xx response from the vendor. Mapped to HTTP 503.
 */
public class VendorUnavailableException extends RuntimeException {

    public VendorUnavailableException(String message) {
        super(message);
    }

    public VendorUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
