package com.bendigobank.commissionquote.dto;

import java.math.BigDecimal;

public record QuoteResponse(
        String quoteId,
        BigDecimal commissionRate,
        BigDecimal totalCommission
) {
}
