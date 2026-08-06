package com.paymentprocessing.payment_processing_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentprocessing.payment_processing_system.dto.CryptoRequest;
import com.paymentprocessing.payment_processing_system.dto.CryptoResponse;
import com.paymentprocessing.payment_processing_system.enums.CryptoConfirmationStatus;
import com.paymentprocessing.payment_processing_system.enums.CryptoCurrency;
import com.paymentprocessing.payment_processing_system.exception.CryptoPaymentException;
import com.paymentprocessing.payment_processing_system.exception.GlobalExceptionHandler;
import com.paymentprocessing.payment_processing_system.service.CryptoPaymentService;
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
class CryptoPaymentControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CryptoPaymentService cryptoPaymentService;

    @InjectMocks
    private CryptoPaymentController cryptoPaymentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cryptoPaymentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createCryptoPayment_shouldReturnCreated() throws Exception {
        CryptoRequest request = buildCryptoRequest();
        CryptoResponse response = buildCryptoResponse(1L);

        when(cryptoPaymentService.createCryptoPayment(any(CryptoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/crypto-payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cryptoId").value(1L));
    }

    @Test
    void getAllCryptoPayments_shouldReturnOk() throws Exception {
        when(cryptoPaymentService.getAllCryptoPayments()).thenReturn(List.of(buildCryptoResponse(1L)));

        mockMvc.perform(get("/crypto-payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cryptoId").value(1L));
    }

    @Test
    void getCryptoPaymentById_shouldReturnOk() throws Exception {
        when(cryptoPaymentService.getCryptoPaymentById(1L)).thenReturn(buildCryptoResponse(1L));

        mockMvc.perform(get("/crypto-payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cryptoId").value(1L));
    }

    @Test
    void getCryptoPaymentById_whenNotFound_shouldReturnBadRequest() throws Exception {
        when(cryptoPaymentService.getCryptoPaymentById(99L))
                .thenThrow(new CryptoPaymentException("Crypto payment not found with id: 99"));

        mockMvc.perform(get("/crypto-payments/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BUSINESS_ERROR"));
    }

    @Test
    void deleteCryptoPayment_shouldReturnOk() throws Exception {
        doNothing().when(cryptoPaymentService).deleteCryptoPayment(1L);

        mockMvc.perform(delete("/crypto-payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Crypto payment deleted successfully"));
    }

    private CryptoRequest buildCryptoRequest() {
        return new CryptoRequest(
                1001L,
                CryptoCurrency.BTC,
                "bc1qexamplewalletaddress",
                "0xabc123txhash",
                "Bitcoin",
                new BigDecimal("0.005"),
                new BigDecimal("62000.50"),
                new BigDecimal("5.25"),
                77L
        );
    }

    private CryptoResponse buildCryptoResponse(Long id) {
        CryptoResponse response = new CryptoResponse();
        response.setCryptoId(id);
        response.setPaymentId(1001L);
        response.setCryptoCurrency(CryptoCurrency.BTC);
        response.setWalletAddress("bc1qexamplewalletaddress");
        response.setTransactionHash("0xabc123txhash");
        response.setBlockchainNetwork("Bitcoin");
        response.setCryptoAmount(new BigDecimal("0.005"));
        response.setExchangeRate(new BigDecimal("62000.50"));
        response.setNetworkFee(new BigDecimal("5.25"));
        response.setExchangeRateId(77L);
        response.setConfirmationStatus(CryptoConfirmationStatus.PENDING);
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }
}
