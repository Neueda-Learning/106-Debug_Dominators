package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.RefundRequest;
import com.paymentprocessing.payment_processing_system.dto.RefundResponse;
import com.paymentprocessing.payment_processing_system.enums.InitiatedBy;
import com.paymentprocessing.payment_processing_system.enums.RefundMethod;
import com.paymentprocessing.payment_processing_system.enums.RefundStatus;
import com.paymentprocessing.payment_processing_system.exception.RefundNotFoundException;
import com.paymentprocessing.payment_processing_system.model.Refund;
import com.paymentprocessing.payment_processing_system.repository.RefundRepository;
import com.paymentprocessing.payment_processing_system.service.impl.RefundServiceImpl;
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
class RefundServiceTest {

    @Mock
    private RefundRepository refundRepository;

    @InjectMocks
    private RefundServiceImpl refundService;

    @Test
    void createRefund_shouldCreateRefundAndSaveToRepository() {
        RefundRequest request = buildRefundRequest();

        when(refundRepository.save(any(Refund.class))).thenAnswer(invocation -> {
            Refund refund = invocation.getArgument(0);
            refund.setRefundId(1L);
            return refund;
        });

        RefundResponse response = refundService.createRefund(request);

        ArgumentCaptor<Refund> refundCaptor = ArgumentCaptor.forClass(Refund.class);
        verify(refundRepository).save(refundCaptor.capture());

        Refund savedEntity = refundCaptor.getValue();
        assertThat(savedEntity.getRefundId()).isEqualTo(1L);
        assertThat(savedEntity.getPaymentId()).isEqualTo(request.getPaymentId());
        assertThat(savedEntity.getRefundReference()).isNotBlank();
        assertThat(savedEntity.getRefundAmount()).isEqualByComparingTo(request.getRefundAmount());
        assertThat(savedEntity.getRefundMethod()).isEqualTo(request.getRefundMethod());
        assertThat(savedEntity.getRefundReason()).isEqualTo(request.getRefundReason());
        assertThat(savedEntity.getInitiatedBy()).isEqualTo(request.getInitiatedBy());
        assertThat(savedEntity.getRefundStatus()).isEqualTo(RefundStatus.REQUESTED);
        assertThat(savedEntity.getRefundDate()).isNotNull();

        assertThat(response.getRefundId()).isEqualTo(1L);
        assertThat(response.getPaymentId()).isEqualTo(request.getPaymentId());
        assertThat(response.getRefundReference()).isEqualTo(savedEntity.getRefundReference());
        assertThat(response.getRefundAmount()).isEqualByComparingTo(request.getRefundAmount());
        assertThat(response.getRefundMethod()).isEqualTo(request.getRefundMethod());
        assertThat(response.getRefundReason()).isEqualTo(request.getRefundReason());
        assertThat(response.getInitiatedBy()).isEqualTo(request.getInitiatedBy());
        assertThat(response.getRefundStatus()).isEqualTo(RefundStatus.REQUESTED);
        assertThat(response.getRefundDate()).isNotNull();
    }

    @Test
    void getRefundById_shouldReturnRefundWhenExists() {
        Long refundId = 10L;
        Refund refund = buildRefundEntity(refundId);
        when(refundRepository.findById(refundId)).thenReturn(Optional.of(refund));

        RefundResponse response = refundService.getRefundById(refundId);

        verify(refundRepository).findById(refundId);
        assertThat(response.getRefundId()).isEqualTo(refund.getRefundId());
        assertThat(response.getPaymentId()).isEqualTo(refund.getPaymentId());
        assertThat(response.getRefundReference()).isEqualTo(refund.getRefundReference());
        assertThat(response.getRefundStatus()).isEqualTo(refund.getRefundStatus());
        assertThat(response.getInitiatedBy()).isEqualTo(refund.getInitiatedBy());
    }

    @Test
    void getRefundById_whenNotFound_shouldThrowRefundNotFoundException() {
        Long refundId = 99L;
        when(refundRepository.findById(refundId)).thenReturn(Optional.empty());

        RefundNotFoundException exception = assertThrows(
                RefundNotFoundException.class,
                () -> refundService.getRefundById(refundId)
        );

        verify(refundRepository).findById(refundId);
        assertThat(exception.getMessage()).isEqualTo("Refund not found with id : 99");
    }

    @Test
    void updateRefund_shouldUpdateAndReturnUpdatedRefundWhenExists() {
        Long refundId = 1L;
        Refund existingRefund = buildRefundEntity(refundId);
        RefundRequest updateRequest = new RefundRequest(
                2002L,
                new BigDecimal("89.99"),
                RefundMethod.BANK_TRANSFER,
                "Partial adjustment",
                InitiatedBy.ADMIN
        );

        when(refundRepository.findById(refundId)).thenReturn(Optional.of(existingRefund));
        when(refundRepository.save(existingRefund)).thenReturn(existingRefund);

        RefundResponse response = refundService.updateRefund(refundId, updateRequest);

        verify(refundRepository).findById(refundId);
        verify(refundRepository).save(existingRefund);

        assertThat(existingRefund.getPaymentId()).isEqualTo(2002L);
        assertThat(existingRefund.getRefundAmount()).isEqualByComparingTo("89.99");
        assertThat(existingRefund.getRefundMethod()).isEqualTo(RefundMethod.BANK_TRANSFER);
        assertThat(existingRefund.getRefundReason()).isEqualTo("Partial adjustment");
        assertThat(existingRefund.getInitiatedBy()).isEqualTo(InitiatedBy.ADMIN);

        assertThat(response.getRefundId()).isEqualTo(refundId);
        assertThat(response.getPaymentId()).isEqualTo(2002L);
        assertThat(response.getRefundAmount()).isEqualByComparingTo("89.99");
        assertThat(response.getRefundMethod()).isEqualTo(RefundMethod.BANK_TRANSFER);
        assertThat(response.getRefundReason()).isEqualTo("Partial adjustment");
        assertThat(response.getInitiatedBy()).isEqualTo(InitiatedBy.ADMIN);
        assertThat(response.getRefundStatus()).isEqualTo(RefundStatus.REQUESTED);
    }

    @Test
    void createRefund_whenRepositorySaveFails_shouldPropagateException() {
        RefundRequest request = buildRefundRequest();
        when(refundRepository.save(any(Refund.class)))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> refundService.createRefund(request)
        );

        verify(refundRepository).save(any(Refund.class));
        assertThat(exception.getMessage()).isEqualTo("Database error");
    }

    @Test
    void updateRefund_whenNotFound_shouldThrowRefundNotFoundException() {
        Long refundId = 2L;
        RefundRequest updateRequest = buildRefundRequest();
        when(refundRepository.findById(refundId)).thenReturn(Optional.empty());

        RefundNotFoundException exception = assertThrows(
                RefundNotFoundException.class,
                () -> refundService.updateRefund(refundId, updateRequest)
        );

        verify(refundRepository).findById(refundId);
        verify(refundRepository, never()).save(any(Refund.class));
        assertThat(exception.getMessage()).isEqualTo("Refund not found with id : 2");
    }

    @Test
    void deleteRefund_shouldDeleteRefundWhenExists() {
        Long refundId = 5L;
        Refund refund = buildRefundEntity(refundId);
        when(refundRepository.findById(refundId)).thenReturn(Optional.of(refund));

        refundService.deleteRefund(refundId);

        verify(refundRepository).findById(refundId);
        verify(refundRepository).delete(refund);
    }

    @Test
    void deleteRefund_whenNotFound_shouldThrowRefundNotFoundException() {
        Long refundId = 6L;
        when(refundRepository.findById(refundId)).thenReturn(Optional.empty());

        RefundNotFoundException exception = assertThrows(
                RefundNotFoundException.class,
                () -> refundService.deleteRefund(refundId)
        );

        verify(refundRepository).findById(refundId);
        verify(refundRepository, never()).delete(any(Refund.class));
        assertThat(exception.getMessage()).isEqualTo("Refund not found with id : 6");
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

    private Refund buildRefundEntity(Long refundId) {
        Refund refund = new Refund();
        refund.setRefundId(refundId);
        refund.setPaymentId(1001L);
        refund.setRefundReference("REFUND-123");
        refund.setRefundAmount(new BigDecimal("150.75"));
        refund.setRefundMethod(RefundMethod.ORIGINAL_PAYMENT_METHOD);
        refund.setRefundReason("Duplicate payment");
        refund.setRefundStatus(RefundStatus.REQUESTED);
        refund.setInitiatedBy(InitiatedBy.CUSTOMER);
        refund.setRefundDate(LocalDateTime.now());
        return refund;
    }
}

