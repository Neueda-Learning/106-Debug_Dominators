package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.PaymentRequest;
import com.paymentprocessing.payment_processing_system.dto.PaymentResponse;
import com.paymentprocessing.payment_processing_system.enums.CurrencyCode;
import com.paymentprocessing.payment_processing_system.enums.PaymentMethod;
import com.paymentprocessing.payment_processing_system.enums.PaymentStatus;
import com.paymentprocessing.payment_processing_system.exception.PaymentNotFoundException;
import com.paymentprocessing.payment_processing_system.model.Payment;
import com.paymentprocessing.payment_processing_system.repository.PaymentRepository;
import com.paymentprocessing.payment_processing_system.service.impl.PaymentServiceImpl;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void createPayment_shouldCreatePaymentAndSaveToRepository() {
        PaymentRequest request = buildPaymentRequest();

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(1L);
            return payment;
        });

        PaymentResponse response = paymentService.createPayment(request);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());

        Payment savedEntity = paymentCaptor.getValue();
        assertThat(savedEntity.getPaymentId()).isNotBlank();
        assertThat(savedEntity.getReferenceNumber()).isNotBlank();
        assertThat(savedEntity.getIdempotencyKey()).isNotBlank();
        assertThat(savedEntity.getSourceAccount()).isEqualTo(request.getSourceAccount());
        assertThat(savedEntity.getDestinationAccount()).isEqualTo(request.getDestinationAccount());
        assertThat(savedEntity.getAmount()).isEqualByComparingTo(request.getAmount());
        assertThat(savedEntity.getCurrency()).isEqualTo(request.getCurrency());
        assertThat(savedEntity.getPaymentMethod()).isEqualTo(request.getPaymentMethod());
        assertThat(savedEntity.getSourceCountry()).isEqualTo(request.getSourceCountry());
        assertThat(savedEntity.getDestinationCountry()).isEqualTo(request.getDestinationCountry());
        assertThat(savedEntity.getDescription()).isEqualTo(request.getDescription());
        assertThat(savedEntity.getRetryCount()).isZero();
        assertThat(savedEntity.getStatus()).isEqualTo(PaymentStatus.CREATED);
        assertThat(savedEntity.getCreatedAt()).isNotNull();
        assertThat(savedEntity.getUpdatedAt()).isNotNull();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getPaymentId()).isEqualTo(savedEntity.getPaymentId());
        assertThat(response.getReferenceNumber()).isEqualTo(savedEntity.getReferenceNumber());
        assertThat(response.getSourceAccount()).isEqualTo(request.getSourceAccount());
        assertThat(response.getDestinationAccount()).isEqualTo(request.getDestinationAccount());
        assertThat(response.getAmount()).isEqualByComparingTo(request.getAmount());
        assertThat(response.getCurrency()).isEqualTo(request.getCurrency());
        assertThat(response.getPaymentMethod()).isEqualTo(request.getPaymentMethod());
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.CREATED);
    }

    @Test
    void createPayment_whenRequestIsNull_shouldThrowExceptionAndNotCallRepository() {
        assertThrows(NullPointerException.class, () -> paymentService.createPayment(null));

        verifyNoInteractions(paymentRepository);
    }

    @Test
    void getPaymentById_shouldReturnPaymentWhenExists() {
        Long paymentId = 10L;
        Payment payment = buildPaymentEntity(paymentId);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentById(paymentId);

        verify(paymentRepository).findById(paymentId);
        assertThat(response.getId()).isEqualTo(payment.getId());
        assertThat(response.getPaymentId()).isEqualTo(payment.getPaymentId());
        assertThat(response.getReferenceNumber()).isEqualTo(payment.getReferenceNumber());
        assertThat(response.getStatus()).isEqualTo(payment.getStatus());
    }

    @Test
    void getPaymentById_whenNotFound_shouldThrowPaymentNotFoundException() {
        Long paymentId = 99L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.getPaymentById(paymentId)
        );

        verify(paymentRepository).findById(paymentId);
        assertThat(exception.getMessage()).isEqualTo("Payment not found with id: 99");
    }

    @Test
    void updatePayment_shouldUpdateAndReturnUpdatedPaymentWhenExists() {
        Long id = 1L;
        Payment existingPayment = buildPaymentEntity(id);
        PaymentRequest updateRequest = new PaymentRequest(
                "SRC-NEW",
                "DST-NEW",
                new BigDecimal("900.00"),
                CurrencyCode.EUR,
                PaymentMethod.CREDIT_CARD,
                "DE",
                "FR",
                "Updated payment"
        );

        when(paymentRepository.findById(id)).thenReturn(Optional.of(existingPayment));
        when(paymentRepository.save(existingPayment)).thenReturn(existingPayment);

        PaymentResponse response = paymentService.updatePayment(id, updateRequest);

        verify(paymentRepository).findById(id);
        verify(paymentRepository).save(existingPayment);

        assertThat(existingPayment.getSourceAccount()).isEqualTo("SRC-NEW");
        assertThat(existingPayment.getDestinationAccount()).isEqualTo("DST-NEW");
        assertThat(existingPayment.getAmount()).isEqualByComparingTo("900.00");
        assertThat(existingPayment.getCurrency()).isEqualTo(CurrencyCode.EUR);
        assertThat(existingPayment.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(existingPayment.getSourceCountry()).isEqualTo("DE");
        assertThat(existingPayment.getDestinationCountry()).isEqualTo("FR");
        assertThat(existingPayment.getDescription()).isEqualTo("Updated payment");
        assertThat(existingPayment.getUpdatedAt()).isNotNull();

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getSourceAccount()).isEqualTo("SRC-NEW");
        assertThat(response.getDestinationAccount()).isEqualTo("DST-NEW");
        assertThat(response.getAmount()).isEqualByComparingTo("900.00");
    }

    @Test
    void updatePayment_whenNotFound_shouldThrowPaymentNotFoundException() {
        Long id = 2L;
        PaymentRequest updateRequest = buildPaymentRequest();
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.updatePayment(id, updateRequest)
        );

        verify(paymentRepository).findById(id);
        verify(paymentRepository, never()).save(any(Payment.class));
        assertThat(exception.getMessage()).isEqualTo("Payment not found with id: 2");
    }

    @Test
    void deletePayment_shouldDeletePaymentWhenExists() {
        Long id = 5L;
        Payment payment = buildPaymentEntity(id);
        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

        paymentService.deletePayment(id);

        verify(paymentRepository).findById(id);
        verify(paymentRepository).delete(payment);
    }

    @Test
    void deletePayment_whenNotFound_shouldThrowPaymentNotFoundException() {
        Long id = 6L;
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.deletePayment(id)
        );

        verify(paymentRepository).findById(id);
        verify(paymentRepository, never()).delete(any(Payment.class));
        assertThat(exception.getMessage()).isEqualTo("Payment not found with id: 6");
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

    private Payment buildPaymentEntity(Long id) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setPaymentId("PAY-123");
        payment.setReferenceNumber("REF-123");
        payment.setSourceAccount("SRC-1001");
        payment.setDestinationAccount("DST-2002");
        payment.setAmount(new BigDecimal("150.75"));
        payment.setCurrency(CurrencyCode.USD);
        payment.setPaymentMethod(PaymentMethod.NET_BANKING);
        payment.setStatus(PaymentStatus.CREATED);
        payment.setSourceCountry("US");
        payment.setDestinationCountry("IN");
        payment.setDescription("Invoice payment");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        return payment;
    }
}

