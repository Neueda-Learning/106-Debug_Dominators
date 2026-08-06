package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.StatementResponse;
import com.paymentprocessing.payment_processing_system.enums.CurrencyCode;
import com.paymentprocessing.payment_processing_system.enums.PaymentMethod;
import com.paymentprocessing.payment_processing_system.enums.PaymentStatus;
import com.paymentprocessing.payment_processing_system.exception.PaymentNotFoundException;
import com.paymentprocessing.payment_processing_system.model.Payment;
import com.paymentprocessing.payment_processing_system.repository.PaymentRepository;
import com.paymentprocessing.payment_processing_system.service.impl.StatementServiceImpl;
import com.paymentprocessing.payment_processing_system.util.PdfGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PdfGenerator pdfGenerator;

    @InjectMocks
    private StatementServiceImpl statementService;

    // ---------------------------------------------------------------
    // 1. SUCCESSFUL STATEMENT GENERATION
    // ---------------------------------------------------------------

    @Test
    void generatePaymentStatement_shouldReturnResourceWhenPaymentExists() {
        Long id = 1L;
        Payment payment = buildPaymentEntity(id);
        byte[] pdfBytes = "PDF_CONTENT".getBytes();

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
        when(pdfGenerator.generateStatement(any(StatementResponse.class))).thenReturn(pdfBytes);

        Resource resource = statementService.generatePaymentStatement(id);

        verify(paymentRepository).findById(id);
        verify(pdfGenerator).generateStatement(any(StatementResponse.class));

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
    }

    @Test
    void generatePaymentStatement_shouldPassCorrectStatementFieldsToPdfGenerator() {
        Long id = 1L;
        Payment payment = buildPaymentEntity(id);
        byte[] pdfBytes = "PDF_CONTENT".getBytes();

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
        when(pdfGenerator.generateStatement(any(StatementResponse.class))).thenReturn(pdfBytes);

        statementService.generatePaymentStatement(id);

        ArgumentCaptor<StatementResponse> captor = ArgumentCaptor.forClass(StatementResponse.class);
        verify(pdfGenerator).generateStatement(captor.capture());

        StatementResponse captured = captor.getValue();
        assertThat(captured.getPaymentId()).isEqualTo(payment.getPaymentId());
        assertThat(captured.getReferenceNumber()).isEqualTo(payment.getReferenceNumber());
        assertThat(captured.getAmount()).isEqualByComparingTo(payment.getAmount());
        assertThat(captured.getCurrency()).isEqualTo(payment.getCurrency().toString());
        assertThat(captured.getStatus()).isEqualTo(payment.getStatus().toString());
        assertThat(captured.getTransactionDate()).isEqualTo(payment.getCreatedAt());
        assertThat(captured.getDescription()).isEqualTo(payment.getDescription());
    }

    // ---------------------------------------------------------------
    // 2. PAYMENT NOT FOUND
    // ---------------------------------------------------------------

    @Test
    void generatePaymentStatement_whenPaymentNotFound_shouldThrowPaymentNotFoundException() {
        Long id = 99L;
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> statementService.generatePaymentStatement(id)
        );

        verify(paymentRepository).findById(id);
        verifyNoInteractions(pdfGenerator);
        assertThat(exception.getMessage()).isEqualTo("Payment not found with id: 99");
    }

    // ---------------------------------------------------------------
    // 3. PDF GENERATION FAILURE
    // ---------------------------------------------------------------

    @Test
    void generatePaymentStatement_whenPdfGeneratorThrows_shouldPropagateException() {
        Long id = 2L;
        Payment payment = buildPaymentEntity(id);

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
        when(pdfGenerator.generateStatement(any(StatementResponse.class)))
                .thenThrow(new RuntimeException("Error generating payment statement"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> statementService.generatePaymentStatement(id)
        );

        verify(paymentRepository).findById(id);
        verify(pdfGenerator).generateStatement(any(StatementResponse.class));
        assertThat(exception.getMessage()).isEqualTo("Error generating payment statement");
    }

    // ---------------------------------------------------------------
    // 4. RESPONSE RESOURCE VALIDATION
    // ---------------------------------------------------------------

    @Test
    void generatePaymentStatement_shouldReturnNonNullReadableResource() {
        Long id = 3L;
        Payment payment = buildPaymentEntity(id);
        byte[] pdfBytes = new byte[]{37, 80, 68, 70}; // %PDF

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
        when(pdfGenerator.generateStatement(any(StatementResponse.class))).thenReturn(pdfBytes);

        Resource resource = statementService.generatePaymentStatement(id);

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
        assertThat(resource.isReadable()).isTrue();
    }

    // ---------------------------------------------------------------
    // 5. REPOSITORY INTERACTION VERIFICATION
    // ---------------------------------------------------------------

    @Test
    void generatePaymentStatement_shouldPassCorrectIdToRepository() {
        Long id = 42L;
        Payment payment = buildPaymentEntity(id);
        byte[] pdfBytes = "PDF".getBytes();

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
        when(pdfGenerator.generateStatement(any(StatementResponse.class))).thenReturn(pdfBytes);

        statementService.generatePaymentStatement(id);

        verify(paymentRepository).findById(id);
        verify(pdfGenerator, never()).generateStatement(argThat(s -> false));
    }

    @Test
    void generatePaymentStatement_whenPaymentNotFound_shouldNeverCallPdfGenerator() {
        Long id = 55L;
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> statementService.generatePaymentStatement(id)
        );

        verify(paymentRepository).findById(id);
        verifyNoInteractions(pdfGenerator);
    }

    // ---------------------------------------------------------------
    // Helper
    // ---------------------------------------------------------------

    private Payment buildPaymentEntity(Long id) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setPaymentId("PAY-" + id);
        payment.setReferenceNumber("REF-" + id);
        payment.setSourceAccount("SRC-1001");
        payment.setDestinationAccount("DST-2002");
        payment.setAmount(new BigDecimal("250.00"));
        payment.setCurrency(CurrencyCode.USD);
        payment.setPaymentMethod(PaymentMethod.NET_BANKING);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setSourceCountry("US");
        payment.setDestinationCountry("IN");
        payment.setDescription("Statement test payment");
        payment.setCreatedAt(LocalDateTime.of(2026, 8, 1, 10, 0));
        payment.setUpdatedAt(LocalDateTime.of(2026, 8, 1, 10, 0));
        return payment;
    }
}


