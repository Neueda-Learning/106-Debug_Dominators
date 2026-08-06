package com.paymentprocessing.payment_processing_system.service.impl;

import com.paymentprocessing.payment_processing_system.dto.AuditLogRequest;
import com.paymentprocessing.payment_processing_system.dto.AuditLogResponse;
import com.paymentprocessing.payment_processing_system.exception.AuditException;
import com.paymentprocessing.payment_processing_system.model.AuditLog;
import com.paymentprocessing.payment_processing_system.repository.AuditLogRepository;
import com.paymentprocessing.payment_processing_system.service.AuditLogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class AuditLogServiceImpl implements AuditLogService {


    private static final Logger log =
            LoggerFactory.getLogger(AuditLogServiceImpl.class);


    private final AuditLogRepository auditLogRepository;


    public AuditLogServiceImpl(
            AuditLogRepository auditLogRepository) {

        this.auditLogRepository = auditLogRepository;
    }



    @Override
    public AuditLogResponse createAuditLog(
            AuditLogRequest request) {


        log.info("Creating audit log for entity: {}",
                request.getEntityName());


        AuditLog auditLog = new AuditLog();


        auditLog.setPaymentId(
                request.getPaymentId()
        );


        auditLog.setEntityName(
                request.getEntityName()
        );


        auditLog.setEntityId(
                request.getEntityId()
        );


        auditLog.setActionType(
                request.getActionType()
        );


        auditLog.setPerformedBy(
                request.getPerformedBy()
        );


        auditLog.setActionDescription(
                request.getActionDescription()
        );


        auditLog.setIpAddress(
                request.getIpAddress()
        );


        auditLog.setRequestId(
                request.getRequestId()
        );


        auditLog.setActionTimestamp(
                LocalDateTime.now()
        );


        AuditLog savedAudit =
                auditLogRepository.save(auditLog);


        log.info("Audit log created successfully with id: {}",
                savedAudit.getAuditId());


        return mapToResponse(savedAudit);
    }




    @Override
    public List<AuditLogResponse> getAllAuditLogs() {


        log.info("Fetching all audit logs");


        List<AuditLogResponse> responses =
                new ArrayList<>();


        auditLogRepository.findAll()
                .forEach(log ->
                        responses.add(
                                mapToResponse(log)
                        ));


        return responses;
    }




    @Override
    public AuditLogResponse getAuditLogById(Long id) {


        log.info("Fetching audit log with id: {}", id);


        AuditLog auditLog =
                auditLogRepository.findById(id)
                        .orElseThrow(() -> {

                            log.error(
                                    "Audit log not found with id: {}",
                                    id
                            );

                            return new AuditException(
                                    "Audit log not found with id: " + id
                            );
                        });


        return mapToResponse(auditLog);
    }




    @Override
    public List<AuditLogResponse> getAuditLogsByPaymentId(
            Long paymentId) {


        log.info(
                "Fetching audit logs for payment id: {}",
                paymentId
        );


        List<AuditLogResponse> responses =
                new ArrayList<>();


        auditLogRepository.findByPaymentId(paymentId)
                .forEach(log ->
                        responses.add(
                                mapToResponse(log)
                        ));


        return responses;
    }




    @Override
    public List<AuditLogResponse> getAuditLogsByEntity(
            String entityName) {


        log.info(
                "Fetching audit logs for entity: {}",
                entityName
        );


        List<AuditLogResponse> responses =
                new ArrayList<>();


        auditLogRepository.findByEntityName(entityName)
                .forEach(log ->
                        responses.add(
                                mapToResponse(log)
                        ));


        return responses;
    }




    @Override
    public void deleteAuditLog(Long id) {


        log.info(
                "Deleting audit log with id: {}",
                id
        );


        if (!auditLogRepository.existsById(id)) {


            log.error(
                    "Audit log not found for deletion with id: {}",
                    id
            );


            throw new AuditException(
                    "Audit log not found with id: " + id
            );
        }


        auditLogRepository.deleteById(id);


        log.info(
                "Audit log deleted successfully with id: {}",
                id
        );
    }




    private AuditLogResponse mapToResponse(
            AuditLog auditLog) {


        AuditLogResponse response =
                new AuditLogResponse();


        response.setAuditId(
                auditLog.getAuditId()
        );


        response.setPaymentId(
                auditLog.getPaymentId()
        );


        response.setEntityName(
                auditLog.getEntityName()
        );


        response.setEntityId(
                auditLog.getEntityId()
        );


        response.setActionType(
                auditLog.getActionType()
        );


        response.setPerformedBy(
                auditLog.getPerformedBy()
        );


        response.setActionDescription(
                auditLog.getActionDescription()
        );


        response.setIpAddress(
                auditLog.getIpAddress()
        );


        response.setActionTimestamp(
                auditLog.getActionTimestamp()
        );


        response.setRequestId(
                auditLog.getRequestId()
        );


        return response;
    }
}