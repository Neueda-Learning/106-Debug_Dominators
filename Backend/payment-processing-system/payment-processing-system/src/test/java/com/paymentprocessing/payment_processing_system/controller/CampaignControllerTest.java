package com.paymentprocessing.payment_processing_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentprocessing.payment_processing_system.dto.CampaignRequest;
import com.paymentprocessing.payment_processing_system.dto.CampaignResponse;
import com.paymentprocessing.payment_processing_system.enums.CampaignCategory;
import com.paymentprocessing.payment_processing_system.enums.CampaignStatus;
import com.paymentprocessing.payment_processing_system.exception.CampaignException;
import com.paymentprocessing.payment_processing_system.exception.GlobalExceptionHandler;
import com.paymentprocessing.payment_processing_system.service.CampaignService;
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
import java.time.LocalDate;
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
class CampaignControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private CampaignController campaignController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(campaignController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createCampaign_shouldReturnCreated() throws Exception {
        CampaignRequest request = buildCampaignRequest();
        CampaignResponse response = buildCampaignResponse(1L);

        when(campaignService.createCampaign(any(CampaignRequest.class))).thenReturn(response);

        mockMvc.perform(post("/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.campaignId").value(1L));
    }

    @Test
    void getAllCampaigns_shouldReturnOk() throws Exception {
        when(campaignService.getAllCampaigns()).thenReturn(List.of(buildCampaignResponse(1L)));

        mockMvc.perform(get("/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].campaignId").value(1L));
    }

    @Test
    void getCampaignById_shouldReturnOk() throws Exception {
        when(campaignService.getCampaignById(1L)).thenReturn(buildCampaignResponse(1L));

        mockMvc.perform(get("/campaigns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaignId").value(1L));
    }

    @Test
    void getCampaignById_whenNotFound_shouldReturnBadRequest() throws Exception {
        when(campaignService.getCampaignById(99L))
                .thenThrow(new CampaignException("Campaign not found with id: 99"));

        mockMvc.perform(get("/campaigns/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BUSINESS_ERROR"));
    }

    @Test
    void deleteCampaign_shouldReturnOk() throws Exception {
        doNothing().when(campaignService).deleteCampaign(1L);

        mockMvc.perform(delete("/campaigns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Campaign deleted successfully"));
    }

    private CampaignRequest buildCampaignRequest() {
        return new CampaignRequest(
                "Help Children",
                "John Organizer",
                CampaignCategory.MEDICAL,
                new BigDecimal("10000.00"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 30),
                "Campaign for healthcare support",
                "system-admin"
        );
    }

    private CampaignResponse buildCampaignResponse(Long id) {
        CampaignResponse response = new CampaignResponse();
        response.setCampaignId(id);
        response.setCampaignCode("CMP123456");
        response.setCampaignTitle("Help Children");
        response.setOrganizerName("John Organizer");
        response.setCategory(CampaignCategory.MEDICAL);
        response.setGoalAmount(new BigDecimal("10000.00"));
        response.setCollectedAmount(BigDecimal.ZERO);
        response.setStartDate(LocalDate.of(2026, 1, 1));
        response.setEndDate(LocalDate.of(2026, 6, 30));
        response.setCampaignStatus(CampaignStatus.ACTIVE);
        response.setDescription("Campaign for healthcare support");
        response.setCreatedBy("system-admin");
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }
}
