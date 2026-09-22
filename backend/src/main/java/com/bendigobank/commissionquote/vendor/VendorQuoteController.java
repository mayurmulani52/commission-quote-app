package com.bendigobank.commissionquote.vendor;

import com.bendigobank.commissionquote.config.VendorProperties;
import com.bendigobank.commissionquote.dto.ErrorResponse;
import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;
import com.bendigobank.commissionquote.exception.InvalidApiKeyException;
import com.bendigobank.commissionquote.exception.VendorUnavailableException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

// Mock of the external vendor's Commission Quote API - reachable over real
// HTTP (not a plain method call) so HttpVendorQuoteClient's timeout/error
// handling actually gets exercised. Only the backend calls this.
@RestController
@RequestMapping("/vendor/commission-quotes")
@Tag(name = "Vendor (mock)", description = "Stand-in for the external vendor - server-to-server only")
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
    @SecurityRequirement(name = "api-key")
    @Operation(
            summary = "Calculate a commission quote (mock vendor)",
            description = "Requires a valid api-key header. Randomly fails ~20% of "
                    + "requests to simulate real vendor network conditions.")
    @ApiResponse(responseCode = "200", description = "Quote calculated successfully")
    @ApiResponse(responseCode = "401", description = "Missing or incorrect api-key header",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "503", description = "Simulated vendor outage",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public QuoteResponse calculateQuote(
            @Parameter(description = "Vendor API key", required = true)
            @RequestHeader(value = "api-key", required = false) String apiKey,
            @Valid @RequestBody QuoteRequest request) {
        if (!isValidApiKey(apiKey)) {
            throw new InvalidApiKeyException("Missing or invalid api-key header");
        }

        if (failureSimulator.shouldFail()) {
            throw new VendorUnavailableException("Simulated vendor outage");
        }

        return commissionCalculator.calculate(request);
    }

    // String.equals() short-circuits on the first mismatched byte, which leaks
    // how much of the key was guessed correctly via response timing. Comparing
    // byte arrays with MessageDigest.isEqual keeps the comparison constant-time.
    private boolean isValidApiKey(String apiKey) {
        if (apiKey == null) {
            return false;
        }
        return MessageDigest.isEqual(
                apiKey.getBytes(StandardCharsets.UTF_8),
                vendorProperties.apiKey().getBytes(StandardCharsets.UTF_8));
    }
}
