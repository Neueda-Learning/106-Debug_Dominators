package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.PaymentHistoryRequest;
import com.paymentprocessing.payment_processing_system.dto.PaymentHistoryResponse;

import java.util.List;

public interface PaymentHistoryService {


    PaymentHistoryResponse createHistory(
            PaymentHistoryRequest request);


    List<PaymentHistoryResponse> getAllHistory();


    PaymentHistoryResponse getHistoryById(Long id);


    List<PaymentHistoryResponse> getHistoryByPaymentId(
            Long paymentId);


    void deleteHistory(Long id);

}