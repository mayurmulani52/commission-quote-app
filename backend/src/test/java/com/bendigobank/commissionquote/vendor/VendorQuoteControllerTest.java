package com.bendigobank.commissionquote.vendor;

import com.bendigobank.commissionquote.config.VendorProperties;
import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;
import com.bendigobank.commissionquote.dto.RiskBand;
import com.bendigobank.commissionquote.exception.InvalidApiKeyException;
import com.bendigobank.commissionquote.exception.VendorUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Plain object test for the api-key check - no Spring context needed.
// VendorApiSecurityIntegrationTest covers the same thing over real HTTP.
class VendorQuoteControllerTest {

    private static final String VALID_KEY = "unit-test-key";

    private FailureSimulator failureSimulator;
    private VendorQuoteController controller;

    @BeforeEach
    void setUp() {
        VendorProperties vendorProperties = new VendorProperties(
                "http://localhost:8080", VALID_KEY, 0.0, 2000, 3000);
        failureSimulator = mock(FailureSimulator.class);
        controller = new VendorQuoteController(vendorProperties, failureSimulator, new CommissionCalculator());
    }

    @Test
    void rejectsRequestMissingApiKeyHeader() {
        assertThrows(InvalidApiKeyException.class, () -> controller.calculateQuote(null, validRequest()));
    }

    @Test
    void rejectsRequestWithBlankApiKeyHeader() {
        assertThrows(InvalidApiKeyException.class, () -> controller.calculateQuote("", validRequest()));
    }

    @Test
    void rejectsRequestWithIncorrectApiKey() {
        assertThrows(InvalidApiKeyException.class,
                () -> controller.calculateQuote("not-the-right-key", validRequest()));
    }

    @Test
    void acceptsRequestWithCorrectApiKeyAndReturnsQuote() {
        when(failureSimulator.shouldFail()).thenReturn(false);

        QuoteResponse response = controller.calculateQuote(VALID_KEY, validRequest());

        assertEquals(0, new BigDecimal("0.0200").compareTo(response.commissionRate()));
    }

    @Test
    void propagatesSimulatedVendorOutageAsVendorUnavailable() {
        when(failureSimulator.shouldFail()).thenReturn(true);

        assertThrows(VendorUnavailableException.class,
                () -> controller.calculateQuote(VALID_KEY, validRequest()));
    }

    private QuoteRequest validRequest() {
        return new QuoteRequest(new BigDecimal("100000"), 360, RiskBand.MEDIUM);
    }
}
