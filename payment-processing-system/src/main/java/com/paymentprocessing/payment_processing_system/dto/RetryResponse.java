package com.paymentprocessing.payment_processing_system.dto;


public class RetryResponse {


    private Long paymentId;

    private Integer retryCount;

    private String status;

    private String message;



    public Long getPaymentId() {
        return paymentId;
    }


    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }


    public Integer getRetryCount() {
        return retryCount;
    }


    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }
}