package com.paymentprocessing.payment_processing_system.service.impl;


import com.paymentprocessing.payment_processing_system.dto.RefundRequest;
import com.paymentprocessing.payment_processing_system.dto.RefundResponse;
import com.paymentprocessing.payment_processing_system.enums.RefundStatus;
import com.paymentprocessing.payment_processing_system.exception.RefundNotFoundException;
import com.paymentprocessing.payment_processing_system.model.Refund;
import com.paymentprocessing.payment_processing_system.repository.RefundRepository;
import com.paymentprocessing.payment_processing_system.service.RefundService;
import com.paymentprocessing.payment_processing_system.util.IdGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Service
@SuppressWarnings("unused")
public class RefundServiceImpl implements RefundService {


    private static final Logger log =
            LoggerFactory.getLogger(RefundServiceImpl.class);



    private final RefundRepository refundRepository;



    public RefundServiceImpl(
            RefundRepository refundRepository) {

        this.refundRepository = refundRepository;
    }





    @Override
    public RefundResponse createRefund(
            RefundRequest refundRequest) {


        log.info(
                "Creating refund for payment id: {}",
                refundRequest.getPaymentId()
        );


        Refund refund =
                mapRequestToEntity(refundRequest);



        Refund savedRefund =
                refundRepository.save(refund);



        log.info(
                "Refund created successfully with id: {}",
                savedRefund.getRefundId()
        );


        return mapToResponse(savedRefund);
    }





    @Override
    public RefundResponse getRefundById(
            Long refundId) {


        log.info(
                "Fetching refund with id: {}",
                refundId
        );


        Refund refund =
                refundRepository.findById(refundId)
                        .orElseThrow(() -> {


                            log.error(
                                    "Refund not found with id: {}",
                                    refundId
                            );


                            return new RefundNotFoundException(
                                    "Refund not found with id : "
                                            + refundId
                            );
                        });



        return mapToResponse(refund);
    }





    @Override
    public List<RefundResponse> getAllRefunds() {


        log.info(
                "Fetching all refunds"
        );


        List<RefundResponse> responses =
                new ArrayList<>();


        refundRepository.findAll()
                .forEach(refund ->
                        responses.add(
                                mapToResponse(refund)
                        ));


        return responses;
    }





    @Override
    public RefundResponse updateRefund(
            Long refundId,
            RefundRequest refundRequest) {


        log.info(
                "Updating refund with id: {}",
                refundId
        );


        Refund refund =
                refundRepository.findById(refundId)
                        .orElseThrow(() -> {


                            log.error(
                                    "Refund not found for update with id: {}",
                                    refundId
                            );


                            return new RefundNotFoundException(
                                    "Refund not found with id : "
                                            + refundId
                            );
                        });



        refund.setPaymentId(
                refundRequest.getPaymentId()
        );


        refund.setRefundAmount(
                refundRequest.getRefundAmount()
        );


        refund.setRefundMethod(
                refundRequest.getRefundMethod()
        );


        refund.setRefundReason(
                refundRequest.getRefundReason()
        );


        refund.setInitiatedBy(
                refundRequest.getInitiatedBy()
        );



        Refund updatedRefund =
                refundRepository.save(refund);



        log.info(
                "Refund updated successfully with id: {}",
                updatedRefund.getRefundId()
        );


        return mapToResponse(updatedRefund);
    }





    @Override
    public void deleteRefund(
            Long refundId) {


        log.info(
                "Deleting refund with id: {}",
                refundId
        );


        Refund refund =
                refundRepository.findById(refundId)
                        .orElseThrow(() -> {


                            log.error(
                                    "Refund not found for deletion with id: {}",
                                    refundId
                            );


                            return new RefundNotFoundException(
                                    "Refund not found with id : "
                                            + refundId
                            );
                        });



        refundRepository.delete(refund);



        log.info(
                "Refund deleted successfully with id: {}",
                refundId
        );
    }





    private Refund mapRequestToEntity(
            RefundRequest request) {


        Refund refund =
                new Refund();


        refund.setPaymentId(
                request.getPaymentId()
        );


        refund.setRefundReference(
                IdGenerator.generateReferenceNumber()
        );


        refund.setRefundAmount(
                request.getRefundAmount()
        );


        refund.setRefundMethod(
                request.getRefundMethod()
        );


        refund.setRefundReason(
                request.getRefundReason()
        );


        refund.setInitiatedBy(
                request.getInitiatedBy()
        );


        refund.setRefundStatus(
                RefundStatus.REQUESTED
        );


        refund.setRefundDate(
                LocalDateTime.now()
        );


        return refund;
    }





    private RefundResponse mapToResponse(
            Refund refund) {


        RefundResponse response =
                new RefundResponse();



        response.setRefundId(
                refund.getRefundId()
        );


        response.setPaymentId(
                refund.getPaymentId()
        );


        response.setRefundReference(
                refund.getRefundReference()
        );


        response.setRefundAmount(
                refund.getRefundAmount()
        );


        response.setRefundMethod(
                refund.getRefundMethod()
        );


        response.setRefundStatus(
                refund.getRefundStatus()
        );


        response.setRefundReason(
                refund.getRefundReason()
        );


        response.setInitiatedBy(
                refund.getInitiatedBy()
        );


        response.setRefundDate(
                refund.getRefundDate()
        );


        return response;
    }
}