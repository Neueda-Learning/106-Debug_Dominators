package com.paymentprocessing.payment_processing_system.dto;

import com.paymentprocessing.payment_processing_system.enums.InitiatedBy;
import com.paymentprocessing.payment_processing_system.enums.RefundMethod;
import com.paymentprocessing.payment_processing_system.enums.RefundStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class RefundResponse {

    private Long refundId;

    private Long paymentId;

    private String refundReference;

    private BigDecimal refundAmount;

    private RefundMethod refundMethod;

    private RefundStatus refundStatus;

    private String refundReason;

    private InitiatedBy initiatedBy;

    private LocalDateTime refundDate;


    // Default Constructor
    public RefundResponse() {
    }


    // Parameterized Constructor
    public RefundResponse(Long refundId,
                          Long paymentId,
                          String refundReference,
                          BigDecimal refundAmount,
                          RefundMethod refundMethod,
                          RefundStatus refundStatus,
                          String refundReason,
                          InitiatedBy initiatedBy,
                          LocalDateTime refundDate) {

        this.refundId = refundId;
        this.paymentId = paymentId;
        this.refundReference = refundReference;
        this.refundAmount = refundAmount;
        this.refundMethod = refundMethod;
        this.refundStatus = refundStatus;
        this.refundReason = refundReason;
        this.initiatedBy = initiatedBy;
        this.refundDate = refundDate;
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


    public RefundStatus getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(RefundStatus refundStatus) {
        this.refundStatus = refundStatus;
    }


    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
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


    @Override
    public String toString() {
        return "RefundResponse{" +
                "refundId=" + refundId +
                ", paymentId=" + paymentId +
                ", refundReference='" + refundReference + '\'' +
                ", refundAmount=" + refundAmount +
                ", refundMethod=" + refundMethod +
                ", refundStatus=" + refundStatus +
                ", refundReason='" + refundReason + '\'' +
                ", initiatedBy=" + initiatedBy +
                ", refundDate=" + refundDate +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefundResponse)) return false;

        RefundResponse that = (RefundResponse) o;

        return Objects.equals(refundId, that.refundId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(refundId);
    }
}