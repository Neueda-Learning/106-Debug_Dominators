package com.paymentprocessing.payment_processing_system.controller;


import com.paymentprocessing.payment_processing_system.dto.AuditLogRequest;
import com.paymentprocessing.payment_processing_system.dto.AuditLogResponse;
import com.paymentprocessing.payment_processing_system.service.AuditLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {


    private final AuditLogService auditLogService;


    public AuditLogController(
            AuditLogService auditLogService) {

        this.auditLogService = auditLogService;
    }



    @PostMapping
    public ResponseEntity<AuditLogResponse> createAuditLog(
            @RequestBody AuditLogRequest request) {


        return new ResponseEntity<>(
                auditLogService.createAuditLog(request),
                HttpStatus.CREATED
        );
    }



    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs() {


        return ResponseEntity.ok(
                auditLogService.getAllAuditLogs()
        );
    }



    @GetMapping("/{id}")
    public ResponseEntity<AuditLogResponse> getAuditLogById(
            @PathVariable Long id) {


        return ResponseEntity.ok(
                auditLogService.getAuditLogById(id)
        );
    }



    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<AuditLogResponse>>
    getByPaymentId(
            @PathVariable Long paymentId) {


        return ResponseEntity.ok(
                auditLogService
                        .getAuditLogsByPaymentId(paymentId)
        );
    }



    @GetMapping("/entity/{entityName}")
    public ResponseEntity<List<AuditLogResponse>>
    getByEntity(
            @PathVariable String entityName) {


        return ResponseEntity.ok(
                auditLogService
                        .getAuditLogsByEntity(entityName)
        );
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuditLog(
            @PathVariable Long id) {


        auditLogService.deleteAuditLog(id);


        return ResponseEntity.ok(
                "Audit log deleted successfully"
        );
    }
}