package com.bendigobank.commissionquote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Bound from vendor.* in application.yml. In production apiKey would come
// from a secrets manager, not an env var with a source-controlled default.
@ConfigurationProperties(prefix = "vendor")
public record VendorProperties(
        String baseUrl,
        String apiKey,
        double failureRate,
        int connectTimeoutMs,
        int readTimeoutMs
) {
}
