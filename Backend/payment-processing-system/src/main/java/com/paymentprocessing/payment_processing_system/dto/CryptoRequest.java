package com.paymentprocessing.payment_processing_system.dto;

import com.paymentprocessing.payment_processing_system.enums.CryptoCurrency;

import java.math.BigDecimal;

public class CryptoRequest {

    private Long paymentId;

    private CryptoCurrency cryptoCurrency;

    private String walletAddress;

    private String transactionHash;

    private String blockchainNetwork;

    private BigDecimal cryptoAmount;

    private BigDecimal exchangeRate;

    private BigDecimal networkFee;

    private Long exchangeRateId;


    // Default Constructor
    public CryptoRequest() {
    }


    // Parameterized Constructor
    public CryptoRequest(Long paymentId,
                         CryptoCurrency cryptoCurrency,
                         String walletAddress,
                         String transactionHash,
                         String blockchainNetwork,
                         BigDecimal cryptoAmount,
                         BigDecimal exchangeRate,
                         BigDecimal networkFee,
                         Long exchangeRateId) {

        this.paymentId = paymentId;
        this.cryptoCurrency = cryptoCurrency;
        this.walletAddress = walletAddress;
        this.transactionHash = transactionHash;
        this.blockchainNetwork = blockchainNetwork;
        this.cryptoAmount = cryptoAmount;
        this.exchangeRate = exchangeRate;
        this.networkFee = networkFee;
        this.exchangeRateId = exchangeRateId;
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


    @Override
    public String toString() {
        return "CryptoRequest{" +
                "paymentId=" + paymentId +
                ", cryptoCurrency=" + cryptoCurrency +
                ", walletAddress='" + walletAddress + '\'' +
                ", transactionHash='" + transactionHash + '\'' +
                ", blockchainNetwork='" + blockchainNetwork + '\'' +
                ", cryptoAmount=" + cryptoAmount +
                ", exchangeRate=" + exchangeRate +
                ", networkFee=" + networkFee +
                ", exchangeRateId=" + exchangeRateId +
                '}';
    }
}