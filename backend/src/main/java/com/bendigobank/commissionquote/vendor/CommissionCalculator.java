package com.bendigobank.commissionquote.vendor;

import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;
import com.bendigobank.commissionquote.dto.RiskBand;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

// Made-up rates for this exercise. Deliberately deterministic (unlike
// vendor availability) so the calculation itself stays easy to test.
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
