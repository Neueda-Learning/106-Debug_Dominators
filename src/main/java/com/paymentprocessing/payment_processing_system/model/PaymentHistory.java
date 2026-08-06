package com.paymentprocessing.payment_processing_system.model;

import com.paymentprocessing.payment_processing_system.enums.PaymentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Table("payment_history")
public class PaymentHistory {

    @Id
    private Long historyId;

    private Long paymentId;

    private PaymentStatus oldStatus;

    private PaymentStatus newStatus;

    private String eventType;

    private String remarks;

    private String changedBy;

    private LocalDateTime changedAt;


    public PaymentHistory() {
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


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof PaymentHistory)) return false;

        PaymentHistory that = (PaymentHistory) o;

        return Objects.equals(historyId, that.historyId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(historyId);
    }
}