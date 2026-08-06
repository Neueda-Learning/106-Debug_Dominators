package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.RetryResponse;
import com.paymentprocessing.payment_processing_system.enums.CurrencyCode;
import com.paymentprocessing.payment_processing_system.enums.PaymentMethod;
import com.paymentprocessing.payment_processing_system.enums.PaymentStatus;
import com.paymentprocessing.payment_processing_system.exception.InvalidStatusTransitionException;
import com.paymentprocessing.payment_processing_system.exception.PaymentNotFoundException;
import com.paymentprocessing.payment_processing_system.model.Payment;
import com.paymentprocessing.payment_processing_system.repository.PaymentRepository;
import com.paymentprocessing.payment_processing_system.service.impl.RetryServiceImpl;
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
class RetryServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private RetryServiceImpl retryService;

    @Test
    void retryPayment_shouldIncrementRetryCountAndSetProcessingStatus() {
        Long id = 1L;
        Payment payment = buildFailedPayment(id, 0);

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        RetryResponse response = retryService.retryPayment(id);

        verify(paymentRepository).findById(id);
        verify(paymentRepository).save(payment);

        assertThat(response.getPaymentId()).isEqualTo(id);
        assertThat(response.getRetryCount()).isEqualTo(1);
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PROCESSING.toString());
        assertThat(response.getMessage()).isEqualTo("Payment retry initiated successfully");
    }

    @Test
    void retryPayment_whenPaymentNotFound_shouldThrowPaymentNotFoundException() {
        Long id = 99L;
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> retryService.retryPayment(id)
        );

        verify(paymentRepository).findById(id);
        verify(paymentRepository, never()).save(any(Payment.class));
        assertThat(exception.getMessage()).isEqualTo("Payment not found with id: 99");
    }

    @Test
    void retryPayment_whenStatusIsNotFailed_shouldThrowInvalidStatusTransitionException() {
        Long id = 2L;
        Payment payment = buildPaymentWithStatus(id, PaymentStatus.COMPLETED, 0);

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

        InvalidStatusTransitionException exception = assertThrows(
                InvalidStatusTransitionException.class,
                () -> retryService.retryPayment(id)
        );

        verify(paymentRepository).findById(id);
        verify(paymentRepository, never()).save(any(Payment.class));
        assertThat(exception.getMessage()).isEqualTo("Only failed payments can be retried");
    }

    @Test
    void retryPayment_shouldSaveUpdatedPaymentToRepository() {
        Long id = 3L;
        Payment payment = buildFailedPayment(id, 1);

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        retryService.retryPayment(id);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());

        Payment saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(PaymentStatus.PROCESSING);
        assertThat(saved.getRetryCount()).isEqualTo(2);
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void retryPayment_shouldIncrementRetryCountFromCurrentValue() {
        Long id = 4L;
        Payment payment = buildFailedPayment(id, 3);

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        RetryResponse response = retryService.retryPayment(id);

        assertThat(response.getRetryCount()).isEqualTo(4);
    }

    @Test
    void retryPayment_whenStatusIsCreated_shouldThrowInvalidStatusTransitionException() {
        Long id = 5L;
        Payment payment = buildPaymentWithStatus(id, PaymentStatus.CREATED, 0);

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

        assertThrows(
                InvalidStatusTransitionException.class,
                () -> retryService.retryPayment(id)
        );

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void retryPayment_whenStatusIsProcessing_shouldThrowInvalidStatusTransitionException() {
        Long id = 6L;
        Payment payment = buildPaymentWithStatus(id, PaymentStatus.PROCESSING, 0);

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

        assertThrows(
                InvalidStatusTransitionException.class,
                () -> retryService.retryPayment(id)
        );

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void retryPayment_shouldUpdateStatusFromFailedToProcessing() {
        Long id = 7L;
        Payment payment = buildFailedPayment(id, 0);

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        retryService.retryPayment(id);
        verify(paymentRepository).save(captor.capture());

        assertThat(captor.getValue().getStatus()).isEqualTo(PaymentStatus.PROCESSING);
    }

    private Payment buildFailedPayment(Long id, int retryCount) {
        return buildPaymentWithStatus(id, PaymentStatus.FAILED, retryCount);
    }

    private Payment buildPaymentWithStatus(Long id, PaymentStatus status, int retryCount) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setPaymentId("PAY-" + id);
        payment.setReferenceNumber("REF-" + id);
        payment.setSourceAccount("SRC-1001");
        payment.setDestinationAccount("DST-2002");
        payment.setAmount(new BigDecimal("150.75"));
        payment.setCurrency(CurrencyCode.USD);
        payment.setPaymentMethod(PaymentMethod.NET_BANKING);
        payment.setStatus(status);
        payment.setRetryCount(retryCount);
        payment.setSourceCountry("US");
        payment.setDestinationCountry("IN");
        payment.setDescription("Invoice payment");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        return payment;
    }
}

