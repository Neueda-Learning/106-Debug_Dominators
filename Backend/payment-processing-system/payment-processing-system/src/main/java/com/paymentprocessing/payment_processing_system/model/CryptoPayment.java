package com.paymentprocessing.payment_processing_system.model;

import com.paymentprocessing.payment_processing_system.enums.CryptoCurrency;
import com.paymentprocessing.payment_processing_system.enums.CryptoConfirmationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Table("crypto_payment")
public class CryptoPayment {

    @Id
    private Long cryptoId;

    private Long paymentId;

    private CryptoCurrency cryptoCurrency;

    private String walletAddress;

    private String transactionHash;

    private String blockchainNetwork;

    private BigDecimal exchangeRate;

    private BigDecimal cryptoAmount;

    private BigDecimal networkFee;

    private CryptoConfirmationStatus confirmationStatus;

    private LocalDateTime createdAt;

    private Long exchangeRateId;


    public CryptoPayment() {
    }


    public Long getCryptoId() {
        return cryptoId;
    }

    public void setCryptoId(Long cryptoId) {
        this.cryptoId = cryptoId;
    }


    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }


    public CryptoCurrency getCryptoCurrency() {
        return cryptoCurrency;
    }

    public void setCryptoCurrency(CryptoCurrency cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }


    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }


    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }


    public String getBlockchainNetwork() {
        return blockchainNetwork;
    }

    public void setBlockchainNetwork(String blockchainNetwork) {
        this.blockchainNetwork = blockchainNetwork;
    }


    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }


    public BigDecimal getCryptoAmount() {
        return cryptoAmount;
    }

    public void setCryptoAmount(BigDecimal cryptoAmount) {
        this.cryptoAmount = cryptoAmount;
    }


    public BigDecimal getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(BigDecimal networkFee) {
        this.networkFee = networkFee;
    }


    public CryptoConfirmationStatus getConfirmationStatus() {
        return confirmationStatus;
    }

    public void setConfirmationStatus(CryptoConfirmationStatus confirmationStatus) {
        this.confirmationStatus = confirmationStatus;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public Long getExchangeRateId() {
        return exchangeRateId;
    }

    public void setExchangeRateId(Long exchangeRateId) {
        this.exchangeRateId = exchangeRateId;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof CryptoPayment)) return false;

        CryptoPayment that = (CryptoPayment) o;

        return Objects.equals(cryptoId, that.cryptoId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(cryptoId);
    }
}