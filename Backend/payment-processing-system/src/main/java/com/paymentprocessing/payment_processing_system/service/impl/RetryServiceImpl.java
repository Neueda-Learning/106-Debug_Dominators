package com.paymentprocessing.payment_processing_system.service.impl;


import com.paymentprocessing.payment_processing_system.dto.RetryResponse;
import com.paymentprocessing.payment_processing_system.enums.PaymentStatus;
import com.paymentprocessing.payment_processing_system.exception.PaymentNotFoundException;
import com.paymentprocessing.payment_processing_system.exception.InvalidStatusTransitionException;
import com.paymentprocessing.payment_processing_system.model.Payment;
import com.paymentprocessing.payment_processing_system.repository.PaymentRepository;
import com.paymentprocessing.payment_processing_system.service.RetryService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class RetryServiceImpl implements RetryService {


    private final PaymentRepository paymentRepository;


    public RetryServiceImpl(
            PaymentRepository paymentRepository) {

        this.paymentRepository = paymentRepository;
    }



    @Override
    public RetryResponse retryPayment(Long paymentId) {


        Payment payment =
                paymentRepository.findById(paymentId)
                        .orElseThrow(() ->
                                new PaymentNotFoundException(
                                        "Payment not found with id: "
                                                + paymentId
                                ));


        if(payment.getStatus() != PaymentStatus.FAILED) {


            throw new InvalidStatusTransitionException(
                    "Only failed payments can be retried"
            );
        }



        Integer currentRetryCount =
                payment.getRetryCount();


        if(currentRetryCount == null) {
            currentRetryCount = 0;
        }


        payment.setRetryCount(
                currentRetryCount + 1
        );


        payment.setStatus(
                PaymentStatus.PROCESSING
        );


        payment.setUpdatedAt(
                LocalDateTime.now()
        );



        Payment updatedPayment =
                paymentRepository.save(payment);



        RetryResponse response =
                new RetryResponse();


        response.setPaymentId(
                updatedPayment.getId()
        );


        response.setRetryCount(
                updatedPayment.getRetryCount()
        );


        response.setStatus(
                updatedPayment.getStatus().toString()
        );


        response.setMessage(
                "Payment retry initiated successfully"
        );


        return response;
    }
}