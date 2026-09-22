package com.bendigobank.commissionquote.exception;

/**
 * Thrown by the mock vendor endpoint when the api-key header is missing
 * or does not match the configured key. Mapped to HTTP 401.
 */
public class InvalidApiKeyException extends RuntimeException {

    public InvalidApiKeyException(String message) {
        super(message);
    }
}
