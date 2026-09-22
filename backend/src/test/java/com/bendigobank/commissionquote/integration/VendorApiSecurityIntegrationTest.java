package com.bendigobank.commissionquote.integration;

import com.bendigobank.commissionquote.dto.ErrorResponse;
import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;
import com.bendigobank.commissionquote.dto.RiskBand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies the mock vendor API's security requirement over real HTTP:
 * "must require an api-key header ... any request without a valid API key
 * should be rejected". Failure-rate is pinned to 0 so only the api-key
 * check is under test here.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "server.port=18083",
        "vendor.failure-rate=0",
        "vendor.api-key=integration-test-key"
})
class VendorApiSecurityIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String VENDOR_PATH = "/vendor/commission-quotes";

    @BeforeEach
    void useJdkHttpClient() {
        // JDK's legacy HttpURLConnection-based factory can't retry a 401/403
        // response once it has started streaming a POST body, and throws
        // HttpRetryException instead of surfacing the response. The
        // java.net.http.HttpClient-backed factory does not have this quirk.
        restTemplate.getRestTemplate().setRequestFactory(new JdkClientHttpRequestFactory());
    }

    @Test
    void rejectsRequestWithNoApiKeyHeader() {
        QuoteRequest request = new QuoteRequest(new BigDecimal("50000"), 12, RiskBand.LOW);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(VENDOR_PATH, request, ErrorResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("UNAUTHORIZED", response.getBody().code());
    }

    @Test
    void rejectsRequestWithIncorrectApiKeyHeader() {
        ResponseEntity<ErrorResponse> response = postWithApiKey("wrong-key", ErrorResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void acceptsRequestWithCorrectApiKeyHeader() {
        ResponseEntity<QuoteResponse> response = postWithApiKey("integration-test-key", QuoteResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private <T> ResponseEntity<T> postWithApiKey(String apiKey, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);
        QuoteRequest request = new QuoteRequest(new BigDecimal("50000"), 12, RiskBand.LOW);
        return restTemplate.postForEntity(VENDOR_PATH, new HttpEntity<>(request, headers), responseType);
    }
}
