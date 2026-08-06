package com.paymentprocessing.payment_processing_system.service.impl;


import com.paymentprocessing.payment_processing_system.dto.ExchangeRateResponse;
import com.paymentprocessing.payment_processing_system.service.ExchangeService;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Service
public class ExchangeServiceImpl implements ExchangeService {


    @Override
    public ExchangeRateResponse convertCurrency(
            String fromCurrency,
            String toCurrency,
            BigDecimal amount) {


        BigDecimal exchangeRate =
                getExchangeRate(
                        fromCurrency,
                        toCurrency
                );


        BigDecimal convertedAmount =
                amount.multiply(exchangeRate)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        ExchangeRateResponse response =
                new ExchangeRateResponse();


        response.setFromCurrency(fromCurrency);

        response.setToCurrency(toCurrency);

        response.setAmount(amount);

        response.setExchangeRate(exchangeRate);

        response.setConvertedAmount(convertedAmount);


        return response;
    }



    private BigDecimal getExchangeRate(
            String from,
            String to) {


        if(from.equals("USD") && to.equals("INR")) {

            return new BigDecimal("83.20");

        }


        if(from.equals("EUR") && to.equals("INR")) {

            return new BigDecimal("90.10");

        }


        if(from.equals("GBP") && to.equals("INR")) {

            return new BigDecimal("105.50");

        }


        // Default same currency

        return BigDecimal.ONE;
    }
}