package com.bendigobank.commissionquote.vendor;

import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;
import com.bendigobank.commissionquote.dto.RiskBand;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommissionCalculatorTest {

    private final CommissionCalculator calculator = new CommissionCalculator();

    @Test
    void appliesOnePercentForLowRisk() {
        QuoteResponse response = calculator.calculate(
                new QuoteRequest(new BigDecimal("100000"), 60, RiskBand.LOW));

        assertEquals(0, new BigDecimal("0.0100").compareTo(response.commissionRate()));
        assertEquals(0, new BigDecimal("1000.00").compareTo(response.totalCommission()));
    }

    @Test
    void appliesTwoPercentForMediumRisk() {
        QuoteResponse response = calculator.calculate(
                new QuoteRequest(new BigDecimal("100000"), 60, RiskBand.MEDIUM));

        assertEquals(0, new BigDecimal("0.0200").compareTo(response.commissionRate()));
        assertEquals(0, new BigDecimal("2000.00").compareTo(response.totalCommission()));
    }

    @Test
    void appliesThreePointFivePercentForHighRisk() {
        QuoteResponse response = calculator.calculate(
                new QuoteRequest(new BigDecimal("100000"), 60, RiskBand.HIGH));

        assertEquals(0, new BigDecimal("0.0350").compareTo(response.commissionRate()));
        assertEquals(0, new BigDecimal("3500.00").compareTo(response.totalCommission()));
    }

    @Test
    void roundsTotalCommissionToTwoDecimalPlaces() {
        QuoteResponse response = calculator.calculate(
                new QuoteRequest(new BigDecimal("999.99"), 12, RiskBand.MEDIUM));

        // 999.99 * 0.02 = 19.9998 -> rounds to 20.00
        assertEquals(0, new BigDecimal("20.00").compareTo(response.totalCommission()));
    }

    @Test
    void generatesUniqueQuoteIdPerCall() {
        Set<String> quoteIds = new HashSet<>();
        QuoteRequest request = new QuoteRequest(new BigDecimal("50000"), 24, RiskBand.LOW);

        for (int i = 0; i < 50; i++) {
            quoteIds.add(calculator.calculate(request).quoteId());
        }

        assertEquals(50, quoteIds.size());
        assertTrue(quoteIds.stream().allMatch(id -> id.startsWith("Q-")));
    }
}
