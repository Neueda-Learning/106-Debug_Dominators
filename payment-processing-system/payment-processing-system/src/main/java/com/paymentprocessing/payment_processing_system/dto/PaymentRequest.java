package com.paymentprocessing.payment_processing_system.dto;

import com.paymentprocessing.payment_processing_system.enums.CurrencyCode;
import com.paymentprocessing.payment_processing_system.enums.PaymentMethod;

import java.math.BigDecimal;

public class PaymentRequest {

    private String sourceAccount;

    private String destinationAccount;

    private BigDecimal amount;

    private CurrencyCode currency;

    private PaymentMethod paymentMethod;

    private String sourceCountry;

    private String destinationCountry;

    private String description;

    public PaymentRequest() {
    }

    public PaymentRequest(String sourceAccount,
                          String destinationAccount,
                          BigDecimal amount,
                          CurrencyCode currency,
                          PaymentMethod paymentMethod,
                          String sourceCountry,
                          String destinationCountry,
                          String description) {
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.sourceCountry = sourceCountry;
        this.destinationCountry = destinationCountry;
        this.description = description;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(String sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getSourceCountry() {
        return sourceCountry;
    }

    public void setSourceCountry(String sourceCountry) {
        this.sourceCountry = sourceCountry;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}