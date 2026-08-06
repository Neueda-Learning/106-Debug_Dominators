package com.paymentprocessing.payment_processing_system.exception;

public class RefundNotFoundException extends RuntimeException {

    public RefundNotFoundException(String message) {
        super(message);
    }
}
