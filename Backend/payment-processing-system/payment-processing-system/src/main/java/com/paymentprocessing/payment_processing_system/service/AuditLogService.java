package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.AuditLogRequest;
import com.paymentprocessing.payment_processing_system.dto.AuditLogResponse;

import java.util.List;

public interface AuditLogService {


    AuditLogResponse createAuditLog(
            AuditLogRequest request);


    List<AuditLogResponse> getAllAuditLogs();


    AuditLogResponse getAuditLogById(Long id);


    List<AuditLogResponse> getAuditLogsByPaymentId(
            Long paymentId);


    List<AuditLogResponse> getAuditLogsByEntity(
            String entityName);


    void deleteAuditLog(Long id);

}