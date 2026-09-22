package com.bendigobank.commissionquote.controller;

import com.bendigobank.commissionquote.dto.ErrorResponse;
import com.bendigobank.commissionquote.dto.QuoteRequest;
import com.bendigobank.commissionquote.dto.QuoteResponse;
import com.bendigobank.commissionquote.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Public-facing API consumed by the frontend. Never talks to the vendor directly - delegates to QuoteService.
@RestController
@RequestMapping("/api/quotes")
@Tag(name = "Quotes", description = "Frontend-facing endpoint for generating a commission quote")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping
    @Operation(
            summary = "Generate a commission quote",
            description = "Validates the loan details, calls the Commission Quote vendor, "
                    + "and returns the resulting quote. No api-key required from callers "
                    + "of this endpoint - that credential is attached server-side only "
                    + "when this backend calls the vendor.")
    @ApiResponse(responseCode = "200", description = "Quote generated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid or malformed request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "503", description = "Vendor timed out, errored, or is simulating an outage",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuoteResponse> generateQuote(@Valid @RequestBody QuoteRequest request) {
        return ResponseEntity.ok(quoteService.generateQuote(request));
    }
}
