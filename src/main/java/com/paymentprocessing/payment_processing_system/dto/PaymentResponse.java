package com.paymentprocessing.payment_processing_system.dto;

import com.paymentprocessing.payment_processing_system.enums.CurrencyCode;
import com.paymentprocessing.payment_processing_system.enums.PaymentMethod;
import com.paymentprocessing.payment_processing_system.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class PaymentResponse {

    private Long id;

    private String paymentId;

    private String referenceNumber;

    private String sourceAccount;

    private String destinationAccount;

    private BigDecimal amount;

    private CurrencyCode currency;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    private String sourceCountry;

    private String destinationCountry;

    private String description;

    private LocalDateTime createdAt;

    public PaymentResponse() {
    }

    public PaymentResponse(Long id,
                           String paymentId,
                           String referenceNumber,
                           String sourceAccount,
                           String destinationAccount,
                           BigDecimal amount,
                           CurrencyCode currency,
                           PaymentMethod paymentMethod,
                           PaymentStatus status,
                           String sourceCountry,
                           String destinationCountry,
                           String description,
                           LocalDateTime createdAt) {
        this.id = id;
        this.paymentId = paymentId;
        this.referenceNumber = referenceNumber;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.sourceCountry = sourceCountry;
        this.destinationCountry = destinationCountry;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
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

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "id=" + id +
                ", paymentId='" + paymentId + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", sourceAccount='" + sourceAccount + '\'' +
                ", destinationAccount='" + destinationAccount + '\'' +
                ", amount=" + amount +
                ", currency=" + currency +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", sourceCountry='" + sourceCountry + '\'' +
                ", destinationCountry='" + destinationCountry + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentResponse)) return false;
        PaymentResponse that = (PaymentResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}