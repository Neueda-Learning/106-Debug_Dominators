package com.paymentprocessing.payment_processing_system.service;


import com.paymentprocessing.payment_processing_system.dto.RetryResponse;


public interface RetryService {


    RetryResponse retryPayment(Long paymentId);

}