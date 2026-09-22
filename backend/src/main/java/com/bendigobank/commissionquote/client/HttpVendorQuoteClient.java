package com.bendigobank.commissionquote.client;

import com.bendigobank.commissionquote.config.VendorProperties;
import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;
import com.bendigobank.commissionquote.exception.VendorUnavailableException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpVendorQuoteClient implements VendorQuoteClient {

    private final RestTemplate restTemplate;
    private final VendorProperties vendorProperties;

    public HttpVendorQuoteClient(RestTemplate vendorRestTemplate, VendorProperties vendorProperties) {
        this.restTemplate = vendorRestTemplate;
        this.vendorProperties = vendorProperties;
    }

    @Override
    public QuoteResponse requestQuote(QuoteRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // The api-key never leaves the backend - the frontend has no knowledge of it.
        headers.set("api-key", vendorProperties.apiKey());

        HttpEntity<QuoteRequest> entity = new HttpEntity<>(request, headers);
        String url = vendorProperties.baseUrl() + "/vendor/commission-quotes";

        try {
            return restTemplate.postForObject(url, entity, QuoteResponse.class);
        } catch (ResourceAccessException ex) {
            // Connection refused / read timeout - vendor unreachable or too slow.
            throw new VendorUnavailableException("Commission quote vendor timed out or is unreachable", ex);
        } catch (HttpStatusCodeException ex) {
            // Vendor responded, but with an error status (e.g. simulated outage, bad api-key).
            throw new VendorUnavailableException(
                    "Commission quote vendor returned an error: " + ex.getStatusCode(), ex);
        }
    }
}
