package com.paymentprocessing.payment_processing_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentprocessing.payment_processing_system.dto.PaymentRequest;
import com.paymentprocessing.payment_processing_system.dto.PaymentResponse;
import com.paymentprocessing.payment_processing_system.enums.CurrencyCode;
import com.paymentprocessing.payment_processing_system.enums.PaymentMethod;
import com.paymentprocessing.payment_processing_system.enums.PaymentStatus;
import com.paymentprocessing.payment_processing_system.exception.GlobalExceptionHandler;
import com.paymentprocessing.payment_processing_system.exception.PaymentNotFoundException;
import com.paymentprocessing.payment_processing_system.service.PaymentService;
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
class PaymentControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createPayment_shouldReturnCreated() throws Exception {
        PaymentRequest request = buildPaymentRequest();
        PaymentResponse response = buildPaymentResponse(1L);

        when(paymentService.createPayment(any(PaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.paymentId").value("PAY-123"));
    }

    @Test
    void getAllPayments_shouldReturnOk() throws Exception {
        when(paymentService.getAllPayments()).thenReturn(List.of(buildPaymentResponse(1L)));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getPaymentById_shouldReturnOk() throws Exception {
        when(paymentService.getPaymentById(1L)).thenReturn(buildPaymentResponse(1L));

        mockMvc.perform(get("/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.paymentId").value("PAY-123"));
    }

    @Test
    void getPaymentById_whenNotFound_shouldReturnNotFound() throws Exception {
        when(paymentService.getPaymentById(99L))
                .thenThrow(new PaymentNotFoundException("Payment not found with id: 99"));

        mockMvc.perform(get("/payments/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("PAYMENT_NOT_FOUND"));
    }

    @Test
    void deletePayment_shouldReturnOk() throws Exception {
        doNothing().when(paymentService).deletePayment(1L);

        mockMvc.perform(delete("/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Payment deleted successfully."));
    }

    private PaymentRequest buildPaymentRequest() {
        return new PaymentRequest(
                "SRC-1001",
                "DST-2002",
                new BigDecimal("150.75"),
                CurrencyCode.USD,
                PaymentMethod.NET_BANKING,
                "US",
                "IN",
                "Invoice payment"
        );
    }

    private PaymentResponse buildPaymentResponse(Long id) {
        PaymentResponse response = new PaymentResponse();
        response.setId(id);
        response.setPaymentId("PAY-123");
        response.setReferenceNumber("REF-123");
        response.setSourceAccount("SRC-1001");
        response.setDestinationAccount("DST-2002");
        response.setAmount(new BigDecimal("150.75"));
        response.setCurrency(CurrencyCode.USD);
        response.setPaymentMethod(PaymentMethod.NET_BANKING);
        response.setStatus(PaymentStatus.CREATED);
        response.setSourceCountry("US");
        response.setDestinationCountry("IN");
        response.setDescription("Invoice payment");
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }
}
