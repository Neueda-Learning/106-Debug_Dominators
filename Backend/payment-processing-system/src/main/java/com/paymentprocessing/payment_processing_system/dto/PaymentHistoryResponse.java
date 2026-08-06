package com.paymentprocessing.payment_processing_system.dto;

import com.paymentprocessing.payment_processing_system.enums.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentHistoryResponse {

    private Long historyId;

    private Long paymentId;

    private PaymentStatus oldStatus;

    private PaymentStatus newStatus;

    private String eventType;

    private String remarks;

    private String changedBy;

    private LocalDateTime changedAt;


    public PaymentHistoryResponse() {
    }


    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }


    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }


    public PaymentStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(PaymentStatus oldStatus) {
        this.oldStatus = oldStatus;
    }


    public PaymentStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(PaymentStatus newStatus) {
        this.newStatus = newStatus;
    }


    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }


    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }


    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}