package com.bendigobank.commissionquote.controller;

import com.bendigobank.commissionquote.dto.QuoteResponse;
import com.bendigobank.commissionquote.exception.VendorUnavailableException;
import com.bendigobank.commissionquote.service.QuoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Validation and error-mapping coverage for the platform-facing controller.
 * The vendor call itself is mocked out here - see {@link com.bendigobank.commissionquote.integration}
 * for tests that exercise the real end-to-end HTTP call chain.
 */
@WebMvcTest(QuoteController.class)
class QuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuoteService quoteService;

    @Test
    void returns200WithQuoteForValidRequest() throws Exception {
        when(quoteService.generateQuote(any())).thenReturn(
                new QuoteResponse("Q-1", new BigDecimal("0.0200"), new BigDecimal("2000.00")));

        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loanAmount\":100000,\"loanTermInMonths\":60,\"riskBand\":\"MEDIUM\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quoteId").value("Q-1"))
                .andExpect(jsonPath("$.totalCommission").value(2000.00));
    }

    @Test
    void returns400ForNegativeLoanAmount() throws Exception {
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loanAmount\":-500,\"loanTermInMonths\":60,\"riskBand\":\"MEDIUM\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void returns400ForZeroLoanAmount() throws Exception {
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loanAmount\":0,\"loanTermInMonths\":60,\"riskBand\":\"MEDIUM\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void returns400ForLoanTermBelowMinimum() throws Exception {
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loanAmount\":100000,\"loanTermInMonths\":0,\"riskBand\":\"MEDIUM\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void returns400ForLoanTermAboveMaximum() throws Exception {
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loanAmount\":100000,\"loanTermInMonths\":481,\"riskBand\":\"MEDIUM\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void returns400ForMissingRiskBand() throws Exception {
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loanAmount\":100000,\"loanTermInMonths\":60}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void returns400ForUnknownRiskBandValue() throws Exception {
        // Guards against malformed / unexpected input values being silently accepted.
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loanAmount\":100000,\"loanTermInMonths\":60,\"riskBand\":\"NOT_A_RISK_BAND\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void returns400ForMalformedJsonBody() throws Exception {
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-valid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void returns503WhenVendorIsUnavailable() throws Exception {
        when(quoteService.generateQuote(any()))
                .thenThrow(new VendorUnavailableException("simulated outage"));

        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loanAmount\":100000,\"loanTermInMonths\":60,\"riskBand\":\"MEDIUM\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("VENDOR_UNAVAILABLE"));
    }
}
