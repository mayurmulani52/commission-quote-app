package com.bendigobank.commissionquote.vendor;

import com.bendigobank.commissionquote.config.VendorProperties;
import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;
import com.bendigobank.commissionquote.exception.InvalidApiKeyException;
import com.bendigobank.commissionquote.exception.VendorUnavailableException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mock implementation of the external vendor's "Commission Quote API".
 * Stands in for the real vendor system while it is under construction.
 *
 * This is deliberately reachable over real HTTP (not just a Java method
 * call) so that timeout/network-failure handling in {@link
 * com.bendigobank.commissionquote.client.HttpVendorQuoteClient} is
 * exercised realistically rather than simulated in-process.
 *
 * Only our own backend ({@link com.bendigobank.commissionquote.controller.QuoteController})
 * is expected to call this - the frontend never talks to it directly, and
 * never sees the api-key.
 */
@RestController
@RequestMapping("/vendor/commission-quotes")
public class VendorQuoteController {

    private final VendorProperties vendorProperties;
    private final FailureSimulator failureSimulator;
    private final CommissionCalculator commissionCalculator;

    public VendorQuoteController(VendorProperties vendorProperties,
                                  FailureSimulator failureSimulator,
                                  CommissionCalculator commissionCalculator) {
        this.vendorProperties = vendorProperties;
        this.failureSimulator = failureSimulator;
        this.commissionCalculator = commissionCalculator;
    }

    @PostMapping
    public QuoteResponse generateQuote(@RequestHeader(value = "api-key", required = false) String apiKey,
                                        @Valid @RequestBody QuoteRequest request) {
        if (apiKey == null || !apiKey.equals(vendorProperties.apiKey())) {
            throw new InvalidApiKeyException("Missing or invalid api-key header");
        }

        if (failureSimulator.shouldFail()) {
            throw new VendorUnavailableException("Simulated vendor outage");
        }

        return commissionCalculator.calculate(request);
    }
}
