package com.bendigobank.commissionquote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

// Same shape for every failure mode (validation, auth, vendor outage) so the
// frontend never has to parse a raw stack trace or framework error body.
public record ErrorResponse(
        @Schema(example = "VALIDATION_ERROR") String code,
        @Schema(example = "One or more fields are invalid") String message,
        @Schema(example = "[\"loanAmount: must be greater than zero\"]") List<String> details
) {
    public ErrorResponse(String code, String message) {
        this(code, message, List.of());
    }
}
