package com.paymentprocessing.payment_processing_system.model;

import com.paymentprocessing.payment_processing_system.enums.AuditActionType;
import com.paymentprocessing.payment_processing_system.enums.PerformedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;


@Table("audit_log")
public class AuditLog {


    @Id
    private Long auditId;


    private Long paymentId;


    private String entityName;


    private Long entityId;


    private AuditActionType actionType;


    private PerformedBy performedBy;


    private String actionDescription;


    private String ipAddress;


    private LocalDateTime actionTimestamp;


    private String requestId;



    public AuditLog() {
    }


    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }


    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }


    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }


    public AuditActionType getActionType() {
        return actionType;
    }

    public void setActionType(AuditActionType actionType) {
        this.actionType = actionType;
    }


    public PerformedBy getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(PerformedBy performedBy) {
        this.performedBy = performedBy;
    }


    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }


    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    public LocalDateTime getActionTimestamp() {
        return actionTimestamp;
    }

    public void setActionTimestamp(LocalDateTime actionTimestamp) {
        this.actionTimestamp = actionTimestamp;
    }


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof AuditLog)) return false;

        AuditLog auditLog = (AuditLog) o;

        return Objects.equals(auditId, auditLog.auditId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(auditId);
    }
}