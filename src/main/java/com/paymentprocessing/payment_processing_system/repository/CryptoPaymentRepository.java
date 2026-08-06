package com.paymentprocessing.payment_processing_system.repository;

import com.paymentprocessing.payment_processing_system.model.CryptoPayment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoPaymentRepository extends CrudRepository<CryptoPayment, Long> {

    List<CryptoPayment> findByPaymentId(Long paymentId);

}