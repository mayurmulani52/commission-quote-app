package com.bendigobank.commissionquote.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Shared request contract between our platform API and the vendor mock -
 * the challenge brief defines a single agreed contract for both.
 */
public record QuoteRequest(
        @NotNull(message = "loanAmount is required")
        @Positive(message = "loanAmount must be greater than zero")
        @Digits(integer = 12, fraction = 2, message = "loanAmount must have at most 2 decimal places")
        BigDecimal loanAmount,

        @NotNull(message = "loanTermInMonths is required")
        @Min(value = 1, message = "loanTermInMonths must be at least 1")
        @Max(value = 480, message = "loanTermInMonths must not exceed 480")
        Integer loanTermInMonths,

        @NotNull(message = "riskBand is required")
        RiskBand riskBand
) {
}
