package com.paymentprocessing.payment_processing_system.model;

import com.paymentprocessing.payment_processing_system.enums.InitiatedBy;
import com.paymentprocessing.payment_processing_system.enums.RefundMethod;
import com.paymentprocessing.payment_processing_system.enums.RefundStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Table("refund")
public class Refund {

    @Id
    private Long refundId;

    private Long paymentId;

    private String refundReference;

    private BigDecimal refundAmount;

    private RefundMethod refundMethod;

    private String refundReason;

    private RefundStatus refundStatus;

    private InitiatedBy initiatedBy;

    private LocalDateTime refundDate;

    private String remarks;

    public Refund() {
    }

    public Refund(Long refundId,
                  Long paymentId,
                  String refundReference,
                  BigDecimal refundAmount,
                  RefundMethod refundMethod,
                  String refundReason,
                  RefundStatus refundStatus,
                  InitiatedBy initiatedBy,
                  LocalDateTime refundDate,
                  String remarks) {

        this.refundId = refundId;
        this.paymentId = paymentId;
        this.refundReference = refundReference;
        this.refundAmount = refundAmount;
        this.refundMethod = refundMethod;
        this.refundReason = refundReason;
        this.refundStatus = refundStatus;
        this.initiatedBy = initiatedBy;
        this.refundDate = refundDate;
        this.remarks = remarks;
    }

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getRefundReference() {
        return refundReference;
    }

    public void setRefundReference(String refundReference) {
        this.refundReference = refundReference;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public RefundMethod getRefundMethod() {
        return refundMethod;
    }

    public void setRefundMethod(RefundMethod refundMethod) {
        this.refundMethod = refundMethod;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public RefundStatus getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(RefundStatus refundStatus) {
        this.refundStatus = refundStatus;
    }

    public InitiatedBy getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(InitiatedBy initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public LocalDateTime getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(LocalDateTime refundDate) {
        this.refundDate = refundDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Refund{" +
                "refundId=" + refundId +
                ", paymentId=" + paymentId +
                ", refundReference='" + refundReference + '\'' +
                ", refundAmount=" + refundAmount +
                ", refundMethod=" + refundMethod +
                ", refundReason='" + refundReason + '\'' +
                ", refundStatus=" + refundStatus +
                ", initiatedBy=" + initiatedBy +
                ", refundDate=" + refundDate +
                ", remarks='" + remarks + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Refund)) return false;
        Refund refund = (Refund) o;
        return Objects.equals(refundId, refund.refundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refundId);
    }
}