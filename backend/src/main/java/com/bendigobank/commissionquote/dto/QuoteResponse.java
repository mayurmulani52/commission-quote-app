package com.bendigobank.commissionquote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record QuoteResponse(
        @Schema(description = "Vendor-generated quote identifier", example = "Q-7AB2DCA0")
        String quoteId,

        @Schema(description = "Commission rate as a decimal fraction", example = "0.02")
        BigDecimal commissionRate,

        @Schema(description = "loanAmount * commissionRate, rounded to 2dp", example = "2000.00")
        BigDecimal totalCommission
) {
}
