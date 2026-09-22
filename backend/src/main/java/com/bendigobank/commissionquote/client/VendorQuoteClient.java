package com.bendigobank.commissionquote.client;

import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;

/**
 * Boundary between our business logic and the external vendor.
 * {@link QuoteService} depends only on this interface, so when the real
 * vendor API becomes available, only {@link HttpVendorQuoteClient} (or a
 * new implementation pointed at the real vendor) needs to change.
 */
public interface VendorQuoteClient {
    QuoteResponse requestQuote(QuoteRequest request);
}
