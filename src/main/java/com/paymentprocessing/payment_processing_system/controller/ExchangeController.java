package com.paymentprocessing.payment_processing_system.controller;


import com.paymentprocessing.payment_processing_system.dto.ExchangeRateResponse;
import com.paymentprocessing.payment_processing_system.service.ExchangeService;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/exchange")
public class ExchangeController {


    private final ExchangeService exchangeService;


    public ExchangeController(
            ExchangeService exchangeService) {

        this.exchangeService = exchangeService;
    }



    @GetMapping("/convert")
    public ExchangeRateResponse convertCurrency(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount) {


        return exchangeService.convertCurrency(
                from,
                to,
                amount
        );
    }
}