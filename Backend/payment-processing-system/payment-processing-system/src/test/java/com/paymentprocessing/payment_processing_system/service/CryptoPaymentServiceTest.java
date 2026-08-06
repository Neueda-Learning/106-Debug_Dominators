package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.CryptoRequest;
import com.paymentprocessing.payment_processing_system.dto.CryptoResponse;
import com.paymentprocessing.payment_processing_system.enums.CryptoConfirmationStatus;
import com.paymentprocessing.payment_processing_system.enums.CryptoCurrency;
import com.paymentprocessing.payment_processing_system.exception.CryptoPaymentException;
import com.paymentprocessing.payment_processing_system.model.CryptoPayment;
import com.paymentprocessing.payment_processing_system.repository.CryptoPaymentRepository;
import com.paymentprocessing.payment_processing_system.service.impl.CryptoPaymentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoPaymentServiceTest {

    @Mock
    private CryptoPaymentRepository cryptoPaymentRepository;

    @InjectMocks
    private CryptoPaymentServiceImpl cryptoPaymentService;

    @Test
    void createCryptoPayment_shouldCreateCryptoPaymentAndSaveToRepository() {
        CryptoRequest request = buildCryptoRequest();

        when(cryptoPaymentRepository.save(any(CryptoPayment.class))).thenAnswer(invocation -> {
            CryptoPayment payment = invocation.getArgument(0);
            payment.setCryptoId(1L);
            return payment;
        });

        CryptoResponse response = cryptoPaymentService.createCryptoPayment(request);

        ArgumentCaptor<CryptoPayment> paymentCaptor = ArgumentCaptor.forClass(CryptoPayment.class);
        verify(cryptoPaymentRepository).save(paymentCaptor.capture());

        CryptoPayment savedEntity = paymentCaptor.getValue();
        assertThat(savedEntity.getCryptoId()).isEqualTo(1L);
        assertThat(savedEntity.getPaymentId()).isEqualTo(request.getPaymentId());
        assertThat(savedEntity.getCryptoCurrency()).isEqualTo(request.getCryptoCurrency());
        assertThat(savedEntity.getWalletAddress()).isEqualTo(request.getWalletAddress());
        assertThat(savedEntity.getTransactionHash()).isEqualTo(request.getTransactionHash());
        assertThat(savedEntity.getBlockchainNetwork()).isEqualTo(request.getBlockchainNetwork());
        assertThat(savedEntity.getCryptoAmount()).isEqualByComparingTo(request.getCryptoAmount());
        assertThat(savedEntity.getExchangeRate()).isEqualByComparingTo(request.getExchangeRate());
        assertThat(savedEntity.getNetworkFee()).isEqualByComparingTo(request.getNetworkFee());
        assertThat(savedEntity.getExchangeRateId()).isEqualTo(request.getExchangeRateId());
        assertThat(savedEntity.getConfirmationStatus()).isEqualTo(CryptoConfirmationStatus.PENDING);
        assertThat(savedEntity.getCreatedAt()).isNotNull();

        assertThat(response.getCryptoId()).isEqualTo(1L);
        assertThat(response.getPaymentId()).isEqualTo(request.getPaymentId());
        assertThat(response.getCryptoCurrency()).isEqualTo(request.getCryptoCurrency());
        assertThat(response.getWalletAddress()).isEqualTo(request.getWalletAddress());
        assertThat(response.getTransactionHash()).isEqualTo(request.getTransactionHash());
        assertThat(response.getBlockchainNetwork()).isEqualTo(request.getBlockchainNetwork());
        assertThat(response.getCryptoAmount()).isEqualByComparingTo(request.getCryptoAmount());
        assertThat(response.getExchangeRate()).isEqualByComparingTo(request.getExchangeRate());
        assertThat(response.getNetworkFee()).isEqualByComparingTo(request.getNetworkFee());
        assertThat(response.getExchangeRateId()).isEqualTo(request.getExchangeRateId());
        assertThat(response.getConfirmationStatus()).isEqualTo(CryptoConfirmationStatus.PENDING);
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    void getCryptoPaymentById_shouldReturnCryptoPaymentWhenExists() {
        Long cryptoId = 10L;
        CryptoPayment payment = buildCryptoPaymentEntity(cryptoId);
        when(cryptoPaymentRepository.findById(cryptoId)).thenReturn(Optional.of(payment));

        CryptoResponse response = cryptoPaymentService.getCryptoPaymentById(cryptoId);

        verify(cryptoPaymentRepository).findById(cryptoId);
        assertThat(response.getCryptoId()).isEqualTo(payment.getCryptoId());
        assertThat(response.getPaymentId()).isEqualTo(payment.getPaymentId());
        assertThat(response.getCryptoCurrency()).isEqualTo(payment.getCryptoCurrency());
        assertThat(response.getWalletAddress()).isEqualTo(payment.getWalletAddress());
        assertThat(response.getTransactionHash()).isEqualTo(payment.getTransactionHash());
        assertThat(response.getBlockchainNetwork()).isEqualTo(payment.getBlockchainNetwork());
        assertThat(response.getCryptoAmount()).isEqualByComparingTo(payment.getCryptoAmount());
        assertThat(response.getExchangeRate()).isEqualByComparingTo(payment.getExchangeRate());
        assertThat(response.getNetworkFee()).isEqualByComparingTo(payment.getNetworkFee());
        assertThat(response.getExchangeRateId()).isEqualTo(payment.getExchangeRateId());
        assertThat(response.getConfirmationStatus()).isEqualTo(payment.getConfirmationStatus());
    }

    @Test
    void getCryptoPaymentById_whenNotFound_shouldThrowCryptoPaymentException() {
        Long cryptoId = 99L;
        when(cryptoPaymentRepository.findById(cryptoId)).thenReturn(Optional.empty());

        CryptoPaymentException exception = assertThrows(
                CryptoPaymentException.class,
                () -> cryptoPaymentService.getCryptoPaymentById(cryptoId)
        );

        verify(cryptoPaymentRepository).findById(cryptoId);
        assertThat(exception.getMessage()).isEqualTo("Crypto payment not found with id: 99");
    }

    @Test
    void deleteCryptoPayment_shouldDeleteCryptoPaymentWhenExists() {
        Long cryptoId = 5L;
        CryptoPayment payment = buildCryptoPaymentEntity(cryptoId);
        when(cryptoPaymentRepository.findById(cryptoId)).thenReturn(Optional.of(payment));

        cryptoPaymentService.deleteCryptoPayment(cryptoId);

        verify(cryptoPaymentRepository).findById(cryptoId);
        verify(cryptoPaymentRepository).delete(payment);
    }

    @Test
    void deleteCryptoPayment_whenNotFound_shouldThrowCryptoPaymentException() {
        Long cryptoId = 6L;
        when(cryptoPaymentRepository.findById(cryptoId)).thenReturn(Optional.empty());

        CryptoPaymentException exception = assertThrows(
                CryptoPaymentException.class,
                () -> cryptoPaymentService.deleteCryptoPayment(cryptoId)
        );

        verify(cryptoPaymentRepository).findById(cryptoId);
        verify(cryptoPaymentRepository, never()).delete(any(CryptoPayment.class));
        assertThat(exception.getMessage()).isEqualTo("Crypto payment not found with id: 6");
    }

    @Test
    void createCryptoPayment_whenRepositorySaveFails_shouldPropagateException() {
        CryptoRequest request = buildCryptoRequest();
        when(cryptoPaymentRepository.save(any(CryptoPayment.class)))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cryptoPaymentService.createCryptoPayment(request)
        );

        verify(cryptoPaymentRepository).save(any(CryptoPayment.class));
        assertThat(exception.getMessage()).isEqualTo("Database error");
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

    private CryptoPayment buildCryptoPaymentEntity(Long cryptoId) {
        CryptoPayment payment = new CryptoPayment();
        payment.setCryptoId(cryptoId);
        payment.setPaymentId(1001L);
        payment.setCryptoCurrency(CryptoCurrency.BTC);
        payment.setWalletAddress("bc1qexamplewalletaddress");
        payment.setTransactionHash("0xabc123txhash");
        payment.setBlockchainNetwork("Bitcoin");
        payment.setCryptoAmount(new BigDecimal("0.005"));
        payment.setExchangeRate(new BigDecimal("62000.50"));
        payment.setNetworkFee(new BigDecimal("5.25"));
        payment.setExchangeRateId(77L);
        payment.setConfirmationStatus(CryptoConfirmationStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        return payment;
    }
}

