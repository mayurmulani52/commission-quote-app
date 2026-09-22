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

// failure-rate=1 forces the vendor to always fail, so this checks end-to-end
// that we return a clean 503 instead of a stack trace or a hang.
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
