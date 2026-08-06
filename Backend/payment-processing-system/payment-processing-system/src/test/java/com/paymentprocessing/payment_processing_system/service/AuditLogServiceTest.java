package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.AuditLogRequest;
import com.paymentprocessing.payment_processing_system.dto.AuditLogResponse;
import com.paymentprocessing.payment_processing_system.enums.AuditActionType;
import com.paymentprocessing.payment_processing_system.enums.PerformedBy;
import com.paymentprocessing.payment_processing_system.exception.AuditException;
import com.paymentprocessing.payment_processing_system.model.AuditLog;
import com.paymentprocessing.payment_processing_system.repository.AuditLogRepository;
import com.paymentprocessing.payment_processing_system.service.impl.AuditLogServiceImpl;
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
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    @Test
    void createAuditLog_shouldCreateAuditLogAndSaveToRepository() {
        AuditLogRequest request = buildAuditLogRequest();

        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog auditLog = invocation.getArgument(0);
            auditLog.setAuditId(1L);
            return auditLog;
        });

        AuditLogResponse response = auditLogService.createAuditLog(request);

        ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(auditCaptor.capture());

        AuditLog savedEntity = auditCaptor.getValue();
        assertThat(savedEntity.getAuditId()).isEqualTo(1L);
        assertThat(savedEntity.getPaymentId()).isEqualTo(request.getPaymentId());
        assertThat(savedEntity.getEntityName()).isEqualTo(request.getEntityName());
        assertThat(savedEntity.getEntityId()).isEqualTo(request.getEntityId());
        assertThat(savedEntity.getActionType()).isEqualTo(request.getActionType());
        assertThat(savedEntity.getPerformedBy()).isEqualTo(request.getPerformedBy());
        assertThat(savedEntity.getActionDescription()).isEqualTo(request.getActionDescription());
        assertThat(savedEntity.getIpAddress()).isEqualTo(request.getIpAddress());
        assertThat(savedEntity.getRequestId()).isEqualTo(request.getRequestId());
        assertThat(savedEntity.getActionTimestamp()).isNotNull();

        assertThat(response.getAuditId()).isEqualTo(1L);
        assertThat(response.getPaymentId()).isEqualTo(request.getPaymentId());
        assertThat(response.getEntityName()).isEqualTo(request.getEntityName());
        assertThat(response.getEntityId()).isEqualTo(request.getEntityId());
        assertThat(response.getActionType()).isEqualTo(request.getActionType());
        assertThat(response.getPerformedBy()).isEqualTo(request.getPerformedBy());
        assertThat(response.getActionDescription()).isEqualTo(request.getActionDescription());
        assertThat(response.getIpAddress()).isEqualTo(request.getIpAddress());
        assertThat(response.getRequestId()).isEqualTo(request.getRequestId());
        assertThat(response.getActionTimestamp()).isNotNull();
    }

    @Test
    void getAuditLogById_shouldReturnAuditLogWhenExists() {
        Long auditId = 10L;
        AuditLog auditLog = buildAuditLogEntity(auditId);
        when(auditLogRepository.findById(auditId)).thenReturn(Optional.of(auditLog));

        AuditLogResponse response = auditLogService.getAuditLogById(auditId);

        verify(auditLogRepository).findById(auditId);
        assertThat(response.getAuditId()).isEqualTo(auditLog.getAuditId());
        assertThat(response.getPaymentId()).isEqualTo(auditLog.getPaymentId());
        assertThat(response.getEntityName()).isEqualTo(auditLog.getEntityName());
        assertThat(response.getActionType()).isEqualTo(auditLog.getActionType());
    }

    @Test
    void getAuditLogById_whenNotFound_shouldThrowAuditException() {
        Long auditId = 99L;
        when(auditLogRepository.findById(auditId)).thenReturn(Optional.empty());

        AuditException exception = assertThrows(
                AuditException.class,
                () -> auditLogService.getAuditLogById(auditId)
        );

        verify(auditLogRepository).findById(auditId);
        assertThat(exception.getMessage()).isEqualTo("Audit log not found with id: 99");
    }

    @Test
    void deleteAuditLog_shouldDeleteAuditLogWhenExists() {
        Long auditId = 5L;
        when(auditLogRepository.existsById(auditId)).thenReturn(true);

        auditLogService.deleteAuditLog(auditId);

        verify(auditLogRepository).existsById(auditId);
        verify(auditLogRepository).deleteById(auditId);
    }

    @Test
    void deleteAuditLog_whenNotFound_shouldThrowAuditException() {
        Long auditId = 6L;
        when(auditLogRepository.existsById(auditId)).thenReturn(false);

        AuditException exception = assertThrows(
                AuditException.class,
                () -> auditLogService.deleteAuditLog(auditId)
        );

        verify(auditLogRepository).existsById(auditId);
        verify(auditLogRepository, never()).deleteById(auditId);
        assertThat(exception.getMessage()).isEqualTo("Audit log not found with id: 6");
    }

    @Test
    void createAuditLog_whenRepositorySaveFails_shouldPropagateException() {
        AuditLogRequest request = buildAuditLogRequest();
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> auditLogService.createAuditLog(request)
        );

        verify(auditLogRepository).save(any(AuditLog.class));
        assertThat(exception.getMessage()).isEqualTo("Database error");
    }

    private AuditLogRequest buildAuditLogRequest() {
        AuditLogRequest request = new AuditLogRequest();
        request.setPaymentId(1001L);
        request.setEntityName("PAYMENT");
        request.setEntityId(2002L);
        request.setActionType(AuditActionType.CREATE);
        request.setPerformedBy(PerformedBy.SYSTEM);
        request.setActionDescription("Payment created");
        request.setIpAddress("127.0.0.1");
        request.setRequestId("REQ-123");
        return request;
    }

    private AuditLog buildAuditLogEntity(Long id) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAuditId(id);
        auditLog.setPaymentId(1001L);
        auditLog.setEntityName("PAYMENT");
        auditLog.setEntityId(2002L);
        auditLog.setActionType(AuditActionType.CREATE);
        auditLog.setPerformedBy(PerformedBy.SYSTEM);
        auditLog.setActionDescription("Payment created");
        auditLog.setIpAddress("127.0.0.1");
        auditLog.setRequestId("REQ-123");
        auditLog.setActionTimestamp(LocalDateTime.now());
        return auditLog;
    }
}

