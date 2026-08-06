package com.paymentprocessing.payment_processing_system.service.impl;


import com.paymentprocessing.payment_processing_system.dto.PaymentHistoryRequest;
import com.paymentprocessing.payment_processing_system.dto.PaymentHistoryResponse;
import com.paymentprocessing.payment_processing_system.exception.ProcessingException;
import com.paymentprocessing.payment_processing_system.model.PaymentHistory;
import com.paymentprocessing.payment_processing_system.repository.PaymentHistoryRepository;
import com.paymentprocessing.payment_processing_system.service.PaymentHistoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Service
public class PaymentHistoryServiceImpl
        implements PaymentHistoryService {



    private static final Logger log =
            LoggerFactory.getLogger(PaymentHistoryServiceImpl.class);



    private final PaymentHistoryRepository repository;



    public PaymentHistoryServiceImpl(
            PaymentHistoryRepository repository) {

        this.repository = repository;
    }





    @Override
    public PaymentHistoryResponse createHistory(
            PaymentHistoryRequest request) {


        log.info(
                "Creating payment history for payment id: {}",
                request.getPaymentId()
        );


        PaymentHistory history = new PaymentHistory();


        history.setPaymentId(
                request.getPaymentId()
        );


        history.setOldStatus(
                request.getOldStatus()
        );


        history.setNewStatus(
                request.getNewStatus()
        );


        history.setEventType(
                request.getEventType()
        );


        history.setRemarks(
                request.getRemarks()
        );


        history.setChangedBy(
                request.getChangedBy()
        );


        history.setChangedAt(
                LocalDateTime.now()
        );


        PaymentHistory savedHistory =
                repository.save(history);



        log.info(
                "Payment history created successfully with id: {}",
                savedHistory.getHistoryId()
        );


        return mapToResponse(savedHistory);
    }





    @Override
    public List<PaymentHistoryResponse> getAllHistory() {


        log.info("Fetching all payment history records");


        List<PaymentHistoryResponse> responses =
                new ArrayList<>();


        repository.findAll()
                .forEach(history ->
                        responses.add(
                                mapToResponse(history)
                        ));


        return responses;
    }





    @Override
    public PaymentHistoryResponse getHistoryById(Long id) {


        log.info(
                "Fetching payment history with id: {}",
                id
        );


        PaymentHistory history =
                repository.findById(id)
                        .orElseThrow(() -> {


                            log.error(
                                    "Payment history not found with id: {}",
                                    id
                            );


                            return new ProcessingException(
                                    "Payment history not found with id: "
                                            + id
                            );
                        });



        return mapToResponse(history);
    }





    @Override
    public List<PaymentHistoryResponse> getHistoryByPaymentId(
            Long paymentId) {


        log.info(
                "Fetching payment history for payment id: {}",
                paymentId
        );


        List<PaymentHistoryResponse> responses =
                new ArrayList<>();


        repository.findByPaymentId(paymentId)
                .forEach(history ->
                        responses.add(
                                mapToResponse(history)
                        ));


        return responses;
    }





    @Override
    public void deleteHistory(Long id) {


        log.info(
                "Deleting payment history with id: {}",
                id
        );


        if (!repository.existsById(id)) {


            log.error(
                    "Payment history not found for deletion with id: {}",
                    id
            );


            throw new ProcessingException(
                    "Payment history not found with id: " + id
            );
        }



        repository.deleteById(id);



        log.info(
                "Payment history deleted successfully with id: {}",
                id
        );
    }





    private PaymentHistoryResponse mapToResponse(
            PaymentHistory history) {


        PaymentHistoryResponse response =
                new PaymentHistoryResponse();


        response.setHistoryId(
                history.getHistoryId()
        );


        response.setPaymentId(
                history.getPaymentId()
        );


        response.setOldStatus(
                history.getOldStatus()
        );


        response.setNewStatus(
                history.getNewStatus()
        );


        response.setEventType(
                history.getEventType()
        );


        response.setRemarks(
                history.getRemarks()
        );


        response.setChangedBy(
                history.getChangedBy()
        );


        response.setChangedAt(
                history.getChangedAt()
        );


        return response;
    }
}