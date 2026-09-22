package com.bendigobank.commissionquote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the (mocked) external Commission Quote vendor.
 * Bound from the "vendor.*" properties in application.yml.
 *
 * In a production environment {@code apiKey} would be sourced from a secrets
 * manager (e.g. AWS Secrets Manager, Vault) rather than an environment
 * variable with a source-controlled default.
 */
@ConfigurationProperties(prefix = "vendor")
public record VendorProperties(
        String baseUrl,
        String apiKey,
        double failureRate,
        int connectTimeoutMs,
        int readTimeoutMs
) {
}
