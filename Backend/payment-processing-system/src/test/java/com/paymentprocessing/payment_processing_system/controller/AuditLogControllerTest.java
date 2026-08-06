package com.paymentprocessing.payment_processing_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentprocessing.payment_processing_system.dto.AuditLogRequest;
import com.paymentprocessing.payment_processing_system.dto.AuditLogResponse;
import com.paymentprocessing.payment_processing_system.enums.AuditActionType;
import com.paymentprocessing.payment_processing_system.enums.PerformedBy;
import com.paymentprocessing.payment_processing_system.exception.AuditException;
import com.paymentprocessing.payment_processing_system.exception.GlobalExceptionHandler;
import com.paymentprocessing.payment_processing_system.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuditLogControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditLogController auditLogController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(auditLogController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createAuditLog_shouldReturnCreated() throws Exception {
        AuditLogRequest request = buildRequest();
        AuditLogResponse response = buildResponse();

        when(auditLogService.createAuditLog(any(AuditLogRequest.class))).thenReturn(response);

        mockMvc.perform(post("/audit-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.auditId").value(1L));
    }

    @Test
    void getAllAuditLogs_shouldReturnOk() throws Exception {
        when(auditLogService.getAllAuditLogs()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].auditId").value(1L));
    }

    @Test
    void getAuditLogById_shouldReturnOk() throws Exception {
        when(auditLogService.getAuditLogById(1L)).thenReturn(buildResponse());

        mockMvc.perform(get("/audit-logs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auditId").value(1L));
    }

    @Test
    void getAuditLogById_whenNotFound_shouldReturnBadRequest() throws Exception {
        when(auditLogService.getAuditLogById(99L))
                .thenThrow(new AuditException("Audit log not found with id: 99"));

        mockMvc.perform(get("/audit-logs/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BUSINESS_ERROR"));
    }

    @Test
    void deleteAuditLog_shouldReturnOk() throws Exception {
        doNothing().when(auditLogService).deleteAuditLog(1L);

        mockMvc.perform(delete("/audit-logs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Audit log deleted successfully"));
    }

    private AuditLogRequest buildRequest() {
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

    private AuditLogResponse buildResponse() {
        AuditLogResponse response = new AuditLogResponse();
        response.setAuditId(1L);
        response.setPaymentId(1001L);
        response.setEntityName("PAYMENT");
        response.setEntityId(2002L);
        response.setActionType(AuditActionType.CREATE);
        response.setPerformedBy(PerformedBy.SYSTEM);
        response.setActionDescription("Payment created");
        response.setIpAddress("127.0.0.1");
        response.setRequestId("REQ-123");
        response.setActionTimestamp(LocalDateTime.now());
        return response;
    }
}
