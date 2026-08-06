package com.paymentprocessing.payment_processing_system.dto;


import java.math.BigDecimal;


public class ExchangeRateResponse {


    private String fromCurrency;

    private String toCurrency;

    private BigDecimal amount;

    private BigDecimal exchangeRate;

    private BigDecimal convertedAmount;



    public String getFromCurrency() {
        return fromCurrency;
    }


    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }


    public String getToCurrency() {
        return toCurrency;
    }


    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }


    public BigDecimal getAmount() {
        return amount;
    }


    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }


    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }


    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }


    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }
}