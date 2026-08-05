package com.paymentprocessing.payment_processing_system.dto;

import com.paymentprocessing.payment_processing_system.enums.CryptoCurrency;
import com.paymentprocessing.payment_processing_system.enums.CryptoConfirmationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CryptoResponse {

    private Long cryptoId;

    private Long paymentId;

    private CryptoCurrency cryptoCurrency;

    private String walletAddress;

    private BigDecimal cryptoAmount;

    private BigDecimal exchangeRate;

    private CryptoConfirmationStatus confirmationStatus;

    private String transactionHash;

    private String blockchainNetwork;

    private BigDecimal networkFee;

    private Long exchangeRateId;

    private LocalDateTime createdAt;


    public CryptoResponse() {
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


    public BigDecimal getCryptoAmount() {
        return cryptoAmount;
    }

    public void setCryptoAmount(BigDecimal cryptoAmount) {
        this.cryptoAmount = cryptoAmount;
    }


    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }


    public CryptoConfirmationStatus getConfirmationStatus() {
        return confirmationStatus;
    }

    public void setConfirmationStatus(CryptoConfirmationStatus confirmationStatus) {
        this.confirmationStatus = confirmationStatus;
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


    public BigDecimal getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(BigDecimal networkFee) {
        this.networkFee = networkFee;
    }


    public Long getExchangeRateId() {
        return exchangeRateId;
    }

    public void setExchangeRateId(Long exchangeRateId) {
        this.exchangeRateId = exchangeRateId;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public String toString() {
        return "CryptoResponse{" +
                "cryptoId=" + cryptoId +
                ", paymentId=" + paymentId +
                ", cryptoCurrency=" + cryptoCurrency +
                ", walletAddress='" + walletAddress + '\'' +
                ", cryptoAmount=" + cryptoAmount +
                ", exchangeRate=" + exchangeRate +
                ", confirmationStatus=" + confirmationStatus +
                ", transactionHash='" + transactionHash + '\'' +
                ", blockchainNetwork='" + blockchainNetwork + '\'' +
                ", networkFee=" + networkFee +
                ", exchangeRateId=" + exchangeRateId +
                ", createdAt=" + createdAt +
                '}';
    }
}