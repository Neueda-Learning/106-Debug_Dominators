package com.paymentprocessing.payment_processing_system.exception;

public class ExchangeRateException extends RuntimeException {

    public ExchangeRateException(String message) {
        super(message);
    }
}