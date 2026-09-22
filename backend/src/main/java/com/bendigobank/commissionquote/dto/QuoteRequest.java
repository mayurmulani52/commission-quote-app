package com.bendigobank.commissionquote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

// Same shape used by both /api/quotes and the vendor mock's /vendor/commission-quotes.
public record QuoteRequest(
        @Schema(description = "Requested loan principal", example = "100000")
        @NotNull(message = "loanAmount is required")
        @Positive(message = "loanAmount must be greater than zero")
        @Digits(integer = 12, fraction = 2, message = "loanAmount must have at most 2 decimal places")
        BigDecimal loanAmount,

        @Schema(description = "Loan term in months (1-480)", example = "60")
        @NotNull(message = "loanTermInMonths is required")
        @Min(value = 1, message = "loanTermInMonths must be at least 1")
        @Max(value = 480, message = "loanTermInMonths must not exceed 480")
        Integer loanTermInMonths,

        @Schema(description = "Applicant's risk band")
        @NotNull(message = "riskBand is required")
        RiskBand riskBand
) {
}
