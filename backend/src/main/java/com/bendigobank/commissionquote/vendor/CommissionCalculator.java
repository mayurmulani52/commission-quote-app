package com.bendigobank.commissionquote.vendor;

import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;
import com.bendigobank.commissionquote.dto.RiskBand;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

/**
 * Deterministic mock business rules for the vendor. These commission rates
 * are made up purely for this exercise, not real Bendigo Bank figures.
 * Deliberately deterministic (not randomised) so that, unlike vendor
 * availability, the commission calculation itself stays easy to test.
 */
@Component
public class CommissionCalculator {

    private static final Map<RiskBand, BigDecimal> RATES_BY_RISK_BAND = Map.of(
            RiskBand.LOW, new BigDecimal("0.0100"),
            RiskBand.MEDIUM, new BigDecimal("0.0200"),
            RiskBand.HIGH, new BigDecimal("0.0350")
    );

    public QuoteResponse calculate(QuoteRequest request) {
        BigDecimal rate = RATES_BY_RISK_BAND.get(request.riskBand());
        BigDecimal totalCommission = request.loanAmount()
                .multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);
        String quoteId = "Q-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new QuoteResponse(quoteId, rate, totalCommission);
    }
}
