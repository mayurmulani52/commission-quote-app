package com.bendigobank.commissionquote.client;

import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;

// Lets QuoteService depend on an interface rather than the HTTP client directly.
public interface VendorQuoteClient {
    QuoteResponse requestQuote(QuoteRequest request);
}
