package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.CryptoRequest;
import com.paymentprocessing.payment_processing_system.dto.CryptoResponse;

import java.util.List;

public interface CryptoPaymentService {

    CryptoResponse createCryptoPayment(CryptoRequest request);

    CryptoResponse getCryptoPaymentById(Long id);

    List<CryptoResponse> getAllCryptoPayments();

    void deleteCryptoPayment(Long id);

}