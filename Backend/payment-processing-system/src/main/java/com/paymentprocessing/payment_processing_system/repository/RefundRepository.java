package com.paymentprocessing.payment_processing_system.repository;

import com.paymentprocessing.payment_processing_system.model.Refund;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends CrudRepository<Refund, Long> {

    Optional<Refund> findByRefundReference(String refundReference);

    List<Refund> findByPaymentId(Long paymentId);

}