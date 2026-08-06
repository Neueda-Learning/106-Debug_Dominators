package com.paymentprocessing.payment_processing_system.repository;


import com.paymentprocessing.payment_processing_system.model.AuditLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AuditLogRepository
        extends CrudRepository<AuditLog, Long> {


    List<AuditLog> findByPaymentId(Long paymentId);


    List<AuditLog> findByEntityName(String entityName);

}
