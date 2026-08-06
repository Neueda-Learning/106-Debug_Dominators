package com.paymentprocessing.payment_processing_system.dto;

import com.paymentprocessing.payment_processing_system.enums.InitiatedBy;
import com.paymentprocessing.payment_processing_system.enums.RefundMethod;

import java.math.BigDecimal;

public class RefundRequest {

    private Long paymentId;

    private BigDecimal refundAmount;

    private RefundMethod refundMethod;

    private String refundReason;

    private InitiatedBy initiatedBy;


    // Default Constructor
    public RefundRequest() {
    }


    // Parameterized Constructor
    public RefundRequest(Long paymentId,
                         BigDecimal refundAmount,
                         RefundMethod refundMethod,
                         String refundReason,
                         InitiatedBy initiatedBy) {

        this.paymentId = paymentId;
        this.refundAmount = refundAmount;
        this.refundMethod = refundMethod;
        this.refundReason = refundReason;
        this.initiatedBy = initiatedBy;
    }


    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
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


    public InitiatedBy getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(InitiatedBy initiatedBy) {
        this.initiatedBy = initiatedBy;
    }


    @Override
    public String toString() {
        return "RefundRequest{" +
                "paymentId=" + paymentId +
                ", refundAmount=" + refundAmount +
                ", refundMethod=" + refundMethod +
                ", refundReason='" + refundReason + '\'' +
                ", initiatedBy=" + initiatedBy +
                '}';
    }
}