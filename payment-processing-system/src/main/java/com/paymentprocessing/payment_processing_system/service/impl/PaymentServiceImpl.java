package com.paymentprocessing.payment_processing_system.service.impl;


import com.paymentprocessing.payment_processing_system.dto.PaymentRequest;
import com.paymentprocessing.payment_processing_system.dto.PaymentResponse;
import com.paymentprocessing.payment_processing_system.enums.PaymentStatus;
import com.paymentprocessing.payment_processing_system.exception.PaymentNotFoundException;
import com.paymentprocessing.payment_processing_system.model.Payment;
import com.paymentprocessing.payment_processing_system.repository.PaymentRepository;
import com.paymentprocessing.payment_processing_system.service.PaymentService;
import com.paymentprocessing.payment_processing_system.util.IdGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Service
public class PaymentServiceImpl implements PaymentService {


    private static final Logger log =
            LoggerFactory.getLogger(PaymentServiceImpl.class);



    private final PaymentRepository paymentRepository;



    public PaymentServiceImpl(
            PaymentRepository paymentRepository) {

        this.paymentRepository = paymentRepository;
    }





    @Override
    public PaymentResponse createPayment(
            PaymentRequest paymentRequest) {


        log.info(
                "Creating payment from account: {} to account: {}",
                paymentRequest.getSourceAccount(),
                paymentRequest.getDestinationAccount()
        );


        Payment payment =
                mapRequestToEntity(paymentRequest);



        Payment savedPayment =
                paymentRepository.save(payment);



        log.info(
                "Payment created successfully with id: {}",
                savedPayment.getId()
        );


        return mapToResponse(savedPayment);
    }





    @Override
    public PaymentResponse getPaymentById(Long id) {


        log.info(
                "Fetching payment with id: {}",
                id
        );


        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() -> {


                            log.error(
                                    "Payment not found with id: {}",
                                    id
                            );


                            return new PaymentNotFoundException(
                                    "Payment not found with id: " + id
                            );
                        });



        return mapToResponse(payment);
    }





    @Override
    public List<PaymentResponse> getAllPayments() {


        log.info(
                "Fetching all payments"
        );


        List<PaymentResponse> responses =
                new ArrayList<>();


        paymentRepository.findAll()
                .forEach(payment ->
                        responses.add(
                                mapToResponse(payment)
                        ));



        return responses;
    }





    @Override
    public PaymentResponse updatePayment(
            Long id,
            PaymentRequest paymentRequest) {


        log.info(
                "Updating payment with id: {}",
                id
        );


        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() -> {


                            log.error(
                                    "Payment not found for update with id: {}",
                                    id
                            );


                            return new PaymentNotFoundException(
                                    "Payment not found with id: " + id
                            );
                        });



        payment.setSourceAccount(
                paymentRequest.getSourceAccount()
        );


        payment.setDestinationAccount(
                paymentRequest.getDestinationAccount()
        );


        payment.setAmount(
                paymentRequest.getAmount()
        );


        payment.setCurrency(
                paymentRequest.getCurrency()
        );


        payment.setPaymentMethod(
                paymentRequest.getPaymentMethod()
        );


        payment.setSourceCountry(
                paymentRequest.getSourceCountry()
        );


        payment.setDestinationCountry(
                paymentRequest.getDestinationCountry()
        );


        payment.setDescription(
                paymentRequest.getDescription()
        );


        payment.setUpdatedAt(
                LocalDateTime.now()
        );



        Payment updatedPayment =
                paymentRepository.save(payment);



        log.info(
                "Payment updated successfully with id: {}",
                updatedPayment.getId()
        );


        return mapToResponse(updatedPayment);
    }





    @Override
    public void deletePayment(Long id) {


        log.info(
                "Deleting payment with id: {}",
                id
        );


        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() -> {


                            log.error(
                                    "Payment not found for deletion with id: {}",
                                    id
                            );


                            return new PaymentNotFoundException(
                                    "Payment not found with id: " + id
                            );
                        });



        paymentRepository.delete(payment);



        log.info(
                "Payment deleted successfully with id: {}",
                id
        );
    }





    private Payment mapRequestToEntity(
            PaymentRequest request) {


        Payment payment =
                new Payment();


        payment.setPaymentId(
                IdGenerator.generatePaymentId()
        );


        payment.setReferenceNumber(
                IdGenerator.generateReferenceNumber()
        );


        payment.setIdempotencyKey(
                IdGenerator.generateIdempotencyKey()
        );


        payment.setSourceAccount(
                request.getSourceAccount()
        );


        payment.setDestinationAccount(
                request.getDestinationAccount()
        );


        payment.setAmount(
                request.getAmount()
        );


        payment.setCurrency(
                request.getCurrency()
        );


        payment.setPaymentMethod(
                request.getPaymentMethod()
        );


        payment.setSourceCountry(
                request.getSourceCountry()
        );


        payment.setDestinationCountry(
                request.getDestinationCountry()
        );


        payment.setDescription(
                request.getDescription()
        );


        payment.setRetryCount(0);


        payment.setStatus(
                PaymentStatus.CREATED
        );


        payment.setCreatedAt(
                LocalDateTime.now()
        );


        payment.setUpdatedAt(
                LocalDateTime.now()
        );


        return payment;
    }





    private PaymentResponse mapToResponse(
            Payment payment) {


        PaymentResponse response =
                new PaymentResponse();



        response.setId(
                payment.getId()
        );


        response.setPaymentId(
                payment.getPaymentId()
        );


        response.setReferenceNumber(
                payment.getReferenceNumber()
        );


        response.setSourceAccount(
                payment.getSourceAccount()
        );


        response.setDestinationAccount(
                payment.getDestinationAccount()
        );


        response.setAmount(
                payment.getAmount()
        );


        response.setCurrency(
                payment.getCurrency()
        );


        response.setPaymentMethod(
                payment.getPaymentMethod()
        );


        response.setStatus(
                payment.getStatus()
        );


        response.setSourceCountry(
                payment.getSourceCountry()
        );


        response.setDestinationCountry(
                payment.getDestinationCountry()
        );


        response.setDescription(
                payment.getDescription()
        );


        response.setCreatedAt(
                payment.getCreatedAt()
        );


        return response;
    }
}