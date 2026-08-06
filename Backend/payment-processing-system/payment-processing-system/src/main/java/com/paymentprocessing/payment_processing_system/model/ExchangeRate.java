package com.paymentprocessing.payment_processing_system.model;

import com.paymentprocessing.payment_processing_system.enums.CurrencyCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Table("exchange_rate")
public class ExchangeRate {

    @Id
    private Long exchangeRateId;

    private CurrencyCode fromCurrency;

    private CurrencyCode toCurrency;

    private BigDecimal exchangeRate;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    private Boolean isActive;

    private LocalDateTime createdAt;

    // Default Constructor
    public ExchangeRate() {
    }

    // Parameterized Constructor
    public ExchangeRate(Long exchangeRateId,
                        CurrencyCode fromCurrency,
                        CurrencyCode toCurrency,
                        BigDecimal exchangeRate,
                        LocalDateTime effectiveFrom,
                        LocalDateTime effectiveTo,
                        Boolean isActive,
                        LocalDateTime createdAt) {
        this.exchangeRateId = exchangeRateId;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.exchangeRate = exchangeRate;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.isActive = (isActive != null) ? isActive : true;
        this.createdAt = createdAt;
    }

    public Long getExchangeRateId() {
        return exchangeRateId;
    }

    public void setExchangeRateId(Long exchangeRateId) {
        this.exchangeRateId = exchangeRateId;
    }

    public CurrencyCode getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(CurrencyCode fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public CurrencyCode getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(CurrencyCode toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public LocalDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDateTime effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "exchangeRateId=" + exchangeRateId +
                ", fromCurrency=" + fromCurrency +
                ", toCurrency=" + toCurrency +
                ", exchangeRate=" + exchangeRate +
                ", effectiveFrom=" + effectiveFrom +
                ", effectiveTo=" + effectiveTo +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeRate)) return false;
        ExchangeRate that = (ExchangeRate) o;
        return Objects.equals(exchangeRateId, that.exchangeRateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exchangeRateId);
    }
}