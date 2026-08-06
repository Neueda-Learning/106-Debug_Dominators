package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.PaymentHistoryRequest;
import com.paymentprocessing.payment_processing_system.dto.PaymentHistoryResponse;
import com.paymentprocessing.payment_processing_system.enums.PaymentStatus;
import com.paymentprocessing.payment_processing_system.exception.ProcessingException;
import com.paymentprocessing.payment_processing_system.model.PaymentHistory;
import com.paymentprocessing.payment_processing_system.repository.PaymentHistoryRepository;
import com.paymentprocessing.payment_processing_system.service.impl.PaymentHistoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentHistoryServiceTest {

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @InjectMocks
    private PaymentHistoryServiceImpl paymentHistoryService;

    @Test
    void createHistory_shouldCreateHistoryAndSaveToRepository() {
        PaymentHistoryRequest request = buildHistoryRequest();

        when(paymentHistoryRepository.save(any(PaymentHistory.class))).thenAnswer(invocation -> {
            PaymentHistory history = invocation.getArgument(0);
            history.setHistoryId(1L);
            return history;
        });

        PaymentHistoryResponse response = paymentHistoryService.createHistory(request);

        ArgumentCaptor<PaymentHistory> historyCaptor = ArgumentCaptor.forClass(PaymentHistory.class);
        verify(paymentHistoryRepository).save(historyCaptor.capture());

        PaymentHistory savedEntity = historyCaptor.getValue();
        assertThat(savedEntity.getHistoryId()).isEqualTo(1L);
        assertThat(savedEntity.getPaymentId()).isEqualTo(request.getPaymentId());
        assertThat(savedEntity.getOldStatus()).isEqualTo(request.getOldStatus());
        assertThat(savedEntity.getNewStatus()).isEqualTo(request.getNewStatus());
        assertThat(savedEntity.getEventType()).isEqualTo(request.getEventType());
        assertThat(savedEntity.getRemarks()).isEqualTo(request.getRemarks());
        assertThat(savedEntity.getChangedBy()).isEqualTo(request.getChangedBy());
        assertThat(savedEntity.getChangedAt()).isNotNull();

        assertThat(response.getHistoryId()).isEqualTo(1L);
        assertThat(response.getPaymentId()).isEqualTo(request.getPaymentId());
        assertThat(response.getOldStatus()).isEqualTo(request.getOldStatus());
        assertThat(response.getNewStatus()).isEqualTo(request.getNewStatus());
        assertThat(response.getEventType()).isEqualTo(request.getEventType());
        assertThat(response.getRemarks()).isEqualTo(request.getRemarks());
        assertThat(response.getChangedBy()).isEqualTo(request.getChangedBy());
        assertThat(response.getChangedAt()).isNotNull();
    }

    @Test
    void getHistoryById_shouldReturnHistoryWhenExists() {
        Long historyId = 10L;
        PaymentHistory history = buildHistoryEntity(historyId);
        when(paymentHistoryRepository.findById(historyId)).thenReturn(Optional.of(history));

        PaymentHistoryResponse response = paymentHistoryService.getHistoryById(historyId);

        verify(paymentHistoryRepository).findById(historyId);
        assertThat(response.getHistoryId()).isEqualTo(history.getHistoryId());
        assertThat(response.getPaymentId()).isEqualTo(history.getPaymentId());
        assertThat(response.getOldStatus()).isEqualTo(history.getOldStatus());
        assertThat(response.getNewStatus()).isEqualTo(history.getNewStatus());
    }

    @Test
    void getHistoryById_whenNotFound_shouldThrowProcessingException() {
        Long historyId = 99L;
        when(paymentHistoryRepository.findById(historyId)).thenReturn(Optional.empty());

        ProcessingException exception = assertThrows(
                ProcessingException.class,
                () -> paymentHistoryService.getHistoryById(historyId)
        );

        verify(paymentHistoryRepository).findById(historyId);
        assertThat(exception.getMessage()).isEqualTo("Payment history not found with id: 99");
    }

    @Test
    void deleteHistory_shouldDeleteHistoryWhenExists() {
        Long historyId = 5L;
        when(paymentHistoryRepository.existsById(historyId)).thenReturn(true);

        paymentHistoryService.deleteHistory(historyId);

        verify(paymentHistoryRepository).existsById(historyId);
        verify(paymentHistoryRepository).deleteById(historyId);
    }

    @Test
    void deleteHistory_whenNotFound_shouldThrowProcessingException() {
        Long historyId = 6L;
        when(paymentHistoryRepository.existsById(historyId)).thenReturn(false);

        ProcessingException exception = assertThrows(
                ProcessingException.class,
                () -> paymentHistoryService.deleteHistory(historyId)
        );

        verify(paymentHistoryRepository).existsById(historyId);
        verify(paymentHistoryRepository, never()).deleteById(historyId);
        assertThat(exception.getMessage()).isEqualTo("Payment history not found with id: 6");
    }

    @Test
    void createHistory_whenRepositorySaveFails_shouldPropagateException() {
        PaymentHistoryRequest request = buildHistoryRequest();
        when(paymentHistoryRepository.save(any(PaymentHistory.class)))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentHistoryService.createHistory(request)
        );

        verify(paymentHistoryRepository).save(any(PaymentHistory.class));
        assertThat(exception.getMessage()).isEqualTo("Database error");
    }

    private PaymentHistoryRequest buildHistoryRequest() {
        PaymentHistoryRequest request = new PaymentHistoryRequest();
        request.setPaymentId(1001L);
        request.setOldStatus(PaymentStatus.CREATED);
        request.setNewStatus(PaymentStatus.PROCESSING);
        request.setEventType("STATUS_CHANGED");
        request.setRemarks("Moved to processing");
        request.setChangedBy("SYSTEM");
        return request;
    }

    private PaymentHistory buildHistoryEntity(Long id) {
        PaymentHistory history = new PaymentHistory();
        history.setHistoryId(id);
        history.setPaymentId(1001L);
        history.setOldStatus(PaymentStatus.CREATED);
        history.setNewStatus(PaymentStatus.PROCESSING);
        history.setEventType("STATUS_CHANGED");
        history.setRemarks("Moved to processing");
        history.setChangedBy("SYSTEM");
        history.setChangedAt(LocalDateTime.now());
        return history;
    }
}

