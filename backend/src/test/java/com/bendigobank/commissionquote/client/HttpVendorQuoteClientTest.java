package com.bendigobank.commissionquote.client;

import com.bendigobank.commissionquote.config.VendorProperties;
import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;
import com.bendigobank.commissionquote.dto.RiskBand;
import com.bendigobank.commissionquote.exception.VendorUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

class HttpVendorQuoteClientTest {

    private static final String VENDOR_URL = "http://localhost:8080/vendor/commission-quotes";

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private HttpVendorQuoteClient client;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        VendorProperties vendorProperties = new VendorProperties(
                "http://localhost:8080", "secret-api-key", 0.0, 2000, 3000);
        client = new HttpVendorQuoteClient(restTemplate, vendorProperties);
    }

    @Test
    void sendsApiKeyHeaderAndParsesSuccessfulResponse() {
        server.expect(requestTo(VENDOR_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("api-key", "secret-api-key"))
                .andRespond(withSuccess(
                        "{\"quoteId\":\"Q-1\",\"commissionRate\":0.02,\"totalCommission\":2000.00}",
                        MediaType.APPLICATION_JSON));

        QuoteResponse response = client.requestQuote(
                new QuoteRequest(new BigDecimal("100000"), 60, RiskBand.MEDIUM));

        assertEquals("Q-1", response.quoteId());
        assertEquals(0, new BigDecimal("0.02").compareTo(response.commissionRate()));
        server.verify();
    }

    @Test
    void wrapsVendorServerErrorAsVendorUnavailable() {
        server.expect(requestTo(VENDOR_URL)).andRespond(withServerError());

        assertThrows(VendorUnavailableException.class, () -> client.requestQuote(
                new QuoteRequest(new BigDecimal("100000"), 60, RiskBand.MEDIUM)));
    }

    @Test
    void wrapsVendorUnauthorizedResponseAsVendorUnavailable() {
        server.expect(requestTo(VENDOR_URL)).andRespond(withUnauthorizedRequest());

        assertThrows(VendorUnavailableException.class, () -> client.requestQuote(
                new QuoteRequest(new BigDecimal("100000"), 60, RiskBand.MEDIUM)));
    }

    @Test
    void wrapsConnectionFailureAsVendorUnavailable() {
        server.expect(requestTo(VENDOR_URL)).andRespond(request -> {
            throw new IOException("simulated network timeout");
        });

        assertThrows(VendorUnavailableException.class, () -> client.requestQuote(
                new QuoteRequest(new BigDecimal("100000"), 60, RiskBand.MEDIUM)));
    }
}
