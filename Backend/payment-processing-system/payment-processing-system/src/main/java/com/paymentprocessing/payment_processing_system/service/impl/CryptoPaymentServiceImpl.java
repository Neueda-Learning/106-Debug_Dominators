package com.paymentprocessing.payment_processing_system.service.impl;


import com.paymentprocessing.payment_processing_system.dto.CryptoRequest;
import com.paymentprocessing.payment_processing_system.dto.CryptoResponse;
import com.paymentprocessing.payment_processing_system.enums.CryptoConfirmationStatus;
import com.paymentprocessing.payment_processing_system.exception.CryptoPaymentException;
import com.paymentprocessing.payment_processing_system.model.CryptoPayment;
import com.paymentprocessing.payment_processing_system.repository.CryptoPaymentRepository;
import com.paymentprocessing.payment_processing_system.service.CryptoPaymentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Service
public class CryptoPaymentServiceImpl implements CryptoPaymentService {


    private static final Logger log =
            LoggerFactory.getLogger(CryptoPaymentServiceImpl.class);



    private final CryptoPaymentRepository cryptoPaymentRepository;



    public CryptoPaymentServiceImpl(
            CryptoPaymentRepository cryptoPaymentRepository) {

        this.cryptoPaymentRepository = cryptoPaymentRepository;
    }





    @Override
    public CryptoResponse createCryptoPayment(
            CryptoRequest request) {


        log.info(
                "Creating crypto payment for payment id: {}",
                request.getPaymentId()
        );


        CryptoPayment cryptoPayment =
                new CryptoPayment();


        cryptoPayment.setPaymentId(
                request.getPaymentId()
        );


        cryptoPayment.setCryptoCurrency(
                request.getCryptoCurrency()
        );


        cryptoPayment.setWalletAddress(
                request.getWalletAddress()
        );


        cryptoPayment.setTransactionHash(
                request.getTransactionHash()
        );


        cryptoPayment.setBlockchainNetwork(
                request.getBlockchainNetwork()
        );


        cryptoPayment.setCryptoAmount(
                request.getCryptoAmount()
        );


        cryptoPayment.setExchangeRate(
                request.getExchangeRate()
        );


        cryptoPayment.setNetworkFee(
                request.getNetworkFee()
        );


        cryptoPayment.setExchangeRateId(
                request.getExchangeRateId()
        );


        cryptoPayment.setConfirmationStatus(
                CryptoConfirmationStatus.PENDING
        );


        cryptoPayment.setCreatedAt(
                LocalDateTime.now()
        );



        CryptoPayment savedPayment =
                cryptoPaymentRepository.save(cryptoPayment);



        log.info(
                "Crypto payment created successfully with id: {}",
                savedPayment.getCryptoId()
        );


        return mapToResponse(savedPayment);
    }





    @Override
    public CryptoResponse getCryptoPaymentById(Long id) {


        log.info(
                "Fetching crypto payment with id: {}",
                id
        );


        CryptoPayment cryptoPayment =
                cryptoPaymentRepository.findById(id)
                        .orElseThrow(() -> {


                            log.error(
                                    "Crypto payment not found with id: {}",
                                    id
                            );


                            return new CryptoPaymentException(
                                    "Crypto payment not found with id: "
                                            + id
                            );
                        });



        return mapToResponse(cryptoPayment);
    }





    @Override
    public List<CryptoResponse> getAllCryptoPayments() {


        log.info(
                "Fetching all crypto payments"
        );


        List<CryptoResponse> responses =
                new ArrayList<>();


        cryptoPaymentRepository.findAll()
                .forEach(payment ->
                        responses.add(
                                mapToResponse(payment)
                        ));


        return responses;
    }





    @Override
    public void deleteCryptoPayment(Long id) {


        log.info(
                "Deleting crypto payment with id: {}",
                id
        );


        CryptoPayment cryptoPayment =
                cryptoPaymentRepository.findById(id)
                        .orElseThrow(() -> {


                            log.error(
                                    "Crypto payment not found for deletion with id: {}",
                                    id
                            );


                            return new CryptoPaymentException(
                                    "Crypto payment not found with id: "
                                            + id
                            );
                        });



        cryptoPaymentRepository.delete(cryptoPayment);



        log.info(
                "Crypto payment deleted successfully with id: {}",
                id
        );
    }





    private CryptoResponse mapToResponse(
            CryptoPayment payment) {


        CryptoResponse response =
                new CryptoResponse();



        response.setCryptoId(
                payment.getCryptoId()
        );


        response.setPaymentId(
                payment.getPaymentId()
        );


        response.setCryptoCurrency(
                payment.getCryptoCurrency()
        );


        response.setWalletAddress(
                payment.getWalletAddress()
        );


        response.setCryptoAmount(
                payment.getCryptoAmount()
        );


        response.setExchangeRate(
                payment.getExchangeRate()
        );


        response.setConfirmationStatus(
                payment.getConfirmationStatus()
        );


        response.setTransactionHash(
                payment.getTransactionHash()
        );


        response.setBlockchainNetwork(
                payment.getBlockchainNetwork()
        );


        response.setNetworkFee(
                payment.getNetworkFee()
        );


        response.setExchangeRateId(
                payment.getExchangeRateId()
        );


        response.setCreatedAt(
                payment.getCreatedAt()
        );


        return response;
    }
}