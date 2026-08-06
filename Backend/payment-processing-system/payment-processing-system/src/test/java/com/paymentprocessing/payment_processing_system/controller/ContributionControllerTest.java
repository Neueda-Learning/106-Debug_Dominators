package com.paymentprocessing.payment_processing_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentprocessing.payment_processing_system.dto.ContributionRequest;
import com.paymentprocessing.payment_processing_system.dto.ContributionResponse;
import com.paymentprocessing.payment_processing_system.enums.ContributionStatus;
import com.paymentprocessing.payment_processing_system.exception.ContributionException;
import com.paymentprocessing.payment_processing_system.exception.GlobalExceptionHandler;
import com.paymentprocessing.payment_processing_system.service.ContributionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ContributionControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ContributionService contributionService;

    @InjectMocks
    private ContributionController contributionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contributionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createContribution_shouldReturnCreated() throws Exception {
        ContributionRequest request = buildContributionRequest();
        ContributionResponse response = buildContributionResponse(1L);

        when(contributionService.createContribution(any(ContributionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contributionId").value(1L));
    }

    @Test
    void getAllContributions_shouldReturnOk() throws Exception {
        when(contributionService.getAllContributions()).thenReturn(List.of(buildContributionResponse(1L)));

        mockMvc.perform(get("/contributions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contributionId").value(1L));
    }

    @Test
    void getContributionById_shouldReturnOk() throws Exception {
        when(contributionService.getContributionById(1L)).thenReturn(buildContributionResponse(1L));

        mockMvc.perform(get("/contributions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contributionId").value(1L));
    }

    @Test
    void getContributionById_whenNotFound_shouldReturnBadRequest() throws Exception {
        when(contributionService.getContributionById(99L))
                .thenThrow(new ContributionException("Contribution not found with id: 99"));

        mockMvc.perform(get("/contributions/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BUSINESS_ERROR"));
    }

    @Test
    void deleteContribution_shouldReturnOk() throws Exception {
        doNothing().when(contributionService).deleteContribution(1L);

        mockMvc.perform(delete("/contributions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Contribution deleted successfully"));
    }

    private ContributionRequest buildContributionRequest() {
        ContributionRequest request = new ContributionRequest();
        request.setCampaignId(1001L);
        request.setPaymentId(2002L);
        request.setContributorName("Alice Donor");
        request.setContributorEmail("alice@example.com");
        request.setContributionAmount(new BigDecimal("75.50"));
        request.setAnonymousDonation(Boolean.FALSE);
        request.setMessage("Keep up the great work");
        return request;
    }

    private ContributionResponse buildContributionResponse(Long id) {
        ContributionResponse response = new ContributionResponse();
        response.setContributionId(id);
        response.setCampaignId(1001L);
        response.setPaymentId(2002L);
        response.setContributorName("Alice Donor");
        response.setContributorEmail("alice@example.com");
        response.setContributionAmount(new BigDecimal("75.50"));
        response.setContributionStatus(ContributionStatus.PENDING);
        response.setAnonymousDonation(Boolean.FALSE);
        response.setMessage("Keep up the great work");
        response.setReceiptNumber("REC123");
        response.setContributionDate(LocalDateTime.now());
        return response;
    }
}
