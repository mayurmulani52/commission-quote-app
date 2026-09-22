package com.bendigobank.commissionquote.integration;

import com.bendigobank.commissionquote.dto.ErrorResponse;
import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;
import com.bendigobank.commissionquote.dto.RiskBand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Full-stack integration test: a real HTTP request hits {@code /api/quotes},
 * which in turn makes a real HTTP call (over loopback) to the mock vendor at
 * {@code /vendor/commission-quotes}. Failure rate is pinned to 0 so the
 * happy path is deterministic; see {@link QuoteVendorOutageIntegrationTest}
 * for the failure path.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "server.port=18081",
        "vendor.failure-rate=0",
        // Our own vendor client needs to call back into this same running instance.
        "vendor.base-url=http://localhost:18081"
})
class QuoteEndToEndIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void generatesQuoteThroughRealVendorHttpCall() {
        QuoteRequest request = new QuoteRequest(new BigDecimal("250000"), 240, RiskBand.HIGH);

        ResponseEntity<QuoteResponse> response = restTemplate.postForEntity("/api/quotes", request, QuoteResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        QuoteResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(0, new BigDecimal("0.0350").compareTo(body.commissionRate()));
        assertEquals(0, new BigDecimal("8750.00").compareTo(body.totalCommission()));
        assertTrue(body.quoteId().startsWith("Q-"));
    }

    @Test
    void rejectsInvalidLoanAmountEndToEnd() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String invalidPayload = "{\"loanAmount\":-100,\"loanTermInMonths\":24,\"riskBand\":\"LOW\"}";

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/api/quotes", new HttpEntity<>(invalidPayload, headers), ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().code());
    }

    @Test
    void rejectsUnreasonablyLargeLoanTermEndToEnd() {
        QuoteRequest request = new QuoteRequest(new BigDecimal("100000"), 9999, RiskBand.LOW);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/quotes", request, ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().code());
    }
}
