package com.paymentprocessing.payment_processing_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentprocessing.payment_processing_system.dto.PaymentHistoryRequest;
import com.paymentprocessing.payment_processing_system.dto.PaymentHistoryResponse;
import com.paymentprocessing.payment_processing_system.enums.PaymentStatus;
import com.paymentprocessing.payment_processing_system.exception.GlobalExceptionHandler;
import com.paymentprocessing.payment_processing_system.exception.ProcessingException;
import com.paymentprocessing.payment_processing_system.service.PaymentHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
class PaymentHistoryControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PaymentHistoryService paymentHistoryService;

    @InjectMocks
    private PaymentHistoryController paymentHistoryController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentHistoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createHistory_shouldReturnCreated() throws Exception {
        PaymentHistoryRequest request = buildRequest();
        PaymentHistoryResponse response = buildResponse(1L);

        when(paymentHistoryService.createHistory(any(PaymentHistoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/payment-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.historyId").value(1L));
    }

    @Test
    void getAllHistory_shouldReturnOk() throws Exception {
        when(paymentHistoryService.getAllHistory()).thenReturn(List.of(buildResponse(1L)));

        mockMvc.perform(get("/payment-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].historyId").value(1L));
    }

    @Test
    void getById_shouldReturnOk() throws Exception {
        when(paymentHistoryService.getHistoryById(1L)).thenReturn(buildResponse(1L));

        mockMvc.perform(get("/payment-history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.historyId").value(1L));
    }

    @Test
    void getById_whenNotFound_shouldReturnBadRequest() throws Exception {
        when(paymentHistoryService.getHistoryById(99L))
                .thenThrow(new ProcessingException("Payment history not found with id: 99"));

        mockMvc.perform(get("/payment-history/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BUSINESS_ERROR"));
    }

    @Test
    void deleteHistory_shouldReturnOk() throws Exception {
        doNothing().when(paymentHistoryService).deleteHistory(1L);

        mockMvc.perform(delete("/payment-history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Payment history deleted successfully"));
    }

    private PaymentHistoryRequest buildRequest() {
        PaymentHistoryRequest request = new PaymentHistoryRequest();
        request.setPaymentId(1001L);
        request.setOldStatus(PaymentStatus.CREATED);
        request.setNewStatus(PaymentStatus.PROCESSING);
        request.setEventType("STATUS_CHANGED");
        request.setRemarks("Moved to processing");
        request.setChangedBy("SYSTEM");
        return request;
    }

    private PaymentHistoryResponse buildResponse(Long id) {
        PaymentHistoryResponse response = new PaymentHistoryResponse();
        response.setHistoryId(id);
        response.setPaymentId(1001L);
        response.setOldStatus(PaymentStatus.CREATED);
        response.setNewStatus(PaymentStatus.PROCESSING);
        response.setEventType("STATUS_CHANGED");
        response.setRemarks("Moved to processing");
        response.setChangedBy("SYSTEM");
        response.setChangedAt(LocalDateTime.now());
        return response;
    }
}
