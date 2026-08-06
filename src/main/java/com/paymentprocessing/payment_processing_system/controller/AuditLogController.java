package com.paymentprocessing.payment_processing_system.controller;


import com.paymentprocessing.payment_processing_system.dto.AuditLogRequest;
import com.paymentprocessing.payment_processing_system.dto.AuditLogResponse;
import com.paymentprocessing.payment_processing_system.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/audit-logs")
@Tag(name = "Audit Log APIs", description = "Operations for recording and retrieving audit log entries")
public class AuditLogController {


    private final AuditLogService auditLogService;


    public AuditLogController(
            AuditLogService auditLogService) {

        this.auditLogService = auditLogService;
    }



    @PostMapping
    @Operation(
            summary = "Create Audit Log",
            description = "Creates a new audit log entry for an application event"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Audit log created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or validation failure"),
            @ApiResponse(responseCode = "404", description = "Referenced resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AuditLogResponse> createAuditLog(
            @RequestBody AuditLogRequest request) {


        return new ResponseEntity<>(
                auditLogService.createAuditLog(request),
                HttpStatus.CREATED
        );
    }



    @GetMapping
    @Operation(
            summary = "Get All Audit Logs",
            description = "Retrieves all audit log entries"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Audit log resources not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs() {


        return ResponseEntity.ok(
                auditLogService.getAllAuditLogs()
        );
    }



    @GetMapping("/{id}")
    @Operation(
            summary = "Get Audit Log By Id",
            description = "Retrieves an audit log entry by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit log retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Audit log not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AuditLogResponse> getAuditLogById(
            @PathVariable Long id) {


        return ResponseEntity.ok(
                auditLogService.getAuditLogById(id)
        );
    }



    @GetMapping("/payment/{paymentId}")
    @Operation(
            summary = "Get Audit Logs By Payment Id",
            description = "Retrieves audit log entries associated with a specific payment id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Payment or audit logs not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AuditLogResponse>>
    getByPaymentId(
            @PathVariable Long paymentId) {


        return ResponseEntity.ok(
                auditLogService
                        .getAuditLogsByPaymentId(paymentId)
        );
    }



    @GetMapping("/entity/{entityName}")
    @Operation(
            summary = "Get Audit Logs By Entity",
            description = "Retrieves audit log entries filtered by entity name"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Entity or audit logs not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AuditLogResponse>>
    getByEntity(
            @PathVariable String entityName) {


        return ResponseEntity.ok(
                auditLogService
                        .getAuditLogsByEntity(entityName)
        );
    }



    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Audit Log",
            description = "Deletes an audit log entry by id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit log deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Audit log not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> deleteAuditLog(
            @PathVariable Long id) {


        auditLogService.deleteAuditLog(id);


        return ResponseEntity.ok(
                "Audit log deleted successfully"
        );
    }
}
