package com.bendigobank.commissionquote.dto;

import java.util.List;

/**
 * Standard error shape returned by the platform API so the frontend always
 * has something predictable to render, instead of leaking raw exception
 * messages/stack traces to the client.
 */
public record ErrorResponse(
        String code,
        String message,
        List<String> details
) {
    public ErrorResponse(String code, String message) {
        this(code, message, List.of());
    }
}
