package com.bendigobank.commissionquote.service;

import com.bendigobank.commissionquote.client.VendorQuoteClient;
import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    private final VendorQuoteClient vendorQuoteClient;

    public QuoteService(VendorQuoteClient vendorQuoteClient) {
        this.vendorQuoteClient = vendorQuoteClient;
    }

    public QuoteResponse generateQuote(QuoteRequest request) {
        return vendorQuoteClient.requestQuote(request);
    }
}
