package com.paymentprocessing.payment_processing_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentprocessing.payment_processing_system.dto.RefundRequest;
import com.paymentprocessing.payment_processing_system.dto.RefundResponse;
import com.paymentprocessing.payment_processing_system.enums.InitiatedBy;
import com.paymentprocessing.payment_processing_system.enums.RefundMethod;
import com.paymentprocessing.payment_processing_system.enums.RefundStatus;
import com.paymentprocessing.payment_processing_system.exception.GlobalExceptionHandler;
import com.paymentprocessing.payment_processing_system.exception.RefundNotFoundException;
import com.paymentprocessing.payment_processing_system.service.RefundService;
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
class RefundControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RefundService refundService;

    @InjectMocks
    private RefundController refundController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(refundController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createRefund_shouldReturnCreated() throws Exception {
        RefundRequest request = buildRefundRequest();
        RefundResponse response = buildRefundResponse(1L);

        when(refundService.createRefund(any(RefundRequest.class))).thenReturn(response);

        mockMvc.perform(post("/refunds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.refundId").value(1L));
    }

    @Test
    void getAllRefunds_shouldReturnOk() throws Exception {
        when(refundService.getAllRefunds()).thenReturn(List.of(buildRefundResponse(1L)));

        mockMvc.perform(get("/refunds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].refundId").value(1L));
    }

    @Test
    void getRefundById_shouldReturnOk() throws Exception {
        when(refundService.getRefundById(1L)).thenReturn(buildRefundResponse(1L));

        mockMvc.perform(get("/refunds/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refundId").value(1L));
    }

    @Test
    void getRefundById_whenNotFound_shouldReturnNotFound() throws Exception {
        when(refundService.getRefundById(99L))
                .thenThrow(new RefundNotFoundException("Refund not found with id : 99"));

        mockMvc.perform(get("/refunds/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("REFUND_NOT_FOUND"));
    }

    @Test
    void deleteRefund_shouldReturnOk() throws Exception {
        doNothing().when(refundService).deleteRefund(1L);

        mockMvc.perform(delete("/refunds/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Refund deleted successfully."));
    }

    private RefundRequest buildRefundRequest() {
        return new RefundRequest(
                1001L,
                new BigDecimal("150.75"),
                RefundMethod.ORIGINAL_PAYMENT_METHOD,
                "Duplicate payment",
                InitiatedBy.CUSTOMER
        );
    }

    private RefundResponse buildRefundResponse(Long id) {
        RefundResponse response = new RefundResponse();
        response.setRefundId(id);
        response.setPaymentId(1001L);
        response.setRefundReference("REFUND-123");
        response.setRefundAmount(new BigDecimal("150.75"));
        response.setRefundMethod(RefundMethod.ORIGINAL_PAYMENT_METHOD);
        response.setRefundStatus(RefundStatus.REQUESTED);
        response.setRefundReason("Duplicate payment");
        response.setInitiatedBy(InitiatedBy.CUSTOMER);
        response.setRefundDate(LocalDateTime.now());
        return response;
    }
}
