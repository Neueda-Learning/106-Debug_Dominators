package com.paymentprocessing.payment_processing_system.repository;

import com.paymentprocessing.payment_processing_system.model.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

    Optional<Payment> findByPaymentId(String paymentId);

    Optional<Payment> findByReferenceNumber(String referenceNumber);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

}