package com.bendigobank.commissionquote.service;

import com.bendigobank.commissionquote.client.VendorQuoteClient;
import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;
import com.bendigobank.commissionquote.dto.RiskBand;
import com.bendigobank.commissionquote.exception.VendorUnavailableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private VendorQuoteClient vendorQuoteClient;

    @Test
    void delegatesToVendorClientAndReturnsItsResponse() {
        QuoteService service = new QuoteService(vendorQuoteClient);
        QuoteRequest request = new QuoteRequest(new BigDecimal("100000"), 60, RiskBand.LOW);
        QuoteResponse expected = new QuoteResponse("Q-ABC123", new BigDecimal("0.0100"), new BigDecimal("1000.00"));
        when(vendorQuoteClient.requestQuote(request)).thenReturn(expected);

        QuoteResponse actual = service.generateQuote(request);

        assertEquals(expected, actual);
    }

    @Test
    void propagatesVendorUnavailableExceptionFromClient() {
        QuoteService service = new QuoteService(vendorQuoteClient);
        QuoteRequest request = new QuoteRequest(new BigDecimal("100000"), 60, RiskBand.LOW);
        when(vendorQuoteClient.requestQuote(any())).thenThrow(new VendorUnavailableException("vendor down"));

        assertThrows(VendorUnavailableException.class, () -> service.generateQuote(request));
    }
}
