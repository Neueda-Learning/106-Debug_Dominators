package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.PaymentRequest;
import com.paymentprocessing.payment_processing_system.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentById(Long id);

    List<PaymentResponse> getAllPayments();

    PaymentResponse updatePayment(Long id, PaymentRequest paymentRequest);

    void deletePayment(Long id);

}