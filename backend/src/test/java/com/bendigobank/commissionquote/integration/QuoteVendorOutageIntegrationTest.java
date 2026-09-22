package com.bendigobank.commissionquote.integration;

import com.bendigobank.commissionquote.dto.ErrorResponse;
import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.RiskBand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Failure-rate is pinned to 1 (always fail) so the vendor-outage path is
 * exercised deterministically end-to-end, proving the platform API
 * gracefully translates a vendor failure into a client-friendly 503
 * rather than leaking a stack trace or hanging.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "server.port=18082",
        "vendor.failure-rate=1",
        "vendor.base-url=http://localhost:18082"
})
class QuoteVendorOutageIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void returns503WithFriendlyMessageWhenVendorAlwaysFails() {
        QuoteRequest request = new QuoteRequest(new BigDecimal("100000"), 12, RiskBand.LOW);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/quotes", request, ErrorResponse.class);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("VENDOR_UNAVAILABLE", body.code());
    }
}
