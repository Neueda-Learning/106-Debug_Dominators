package com.paymentprocessing.payment_processing_system.service;


import com.paymentprocessing.payment_processing_system.dto.ExchangeRateResponse;

import java.math.BigDecimal;


public interface ExchangeService {


    ExchangeRateResponse convertCurrency(
            String fromCurrency,
            String toCurrency,
            BigDecimal amount
    );

}