package com.paymentprocessing.payment_processing_system.repository;

import com.paymentprocessing.payment_processing_system.model.PaymentHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentHistoryRepository
        extends CrudRepository<PaymentHistory, Long> {


    List<PaymentHistory> findByPaymentId(Long paymentId);

}