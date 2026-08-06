package com.paymentprocessing.payment_processing_system.repository;


import com.paymentprocessing.payment_processing_system.model.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationRepository
        extends CrudRepository<Notification, Long> {


    List<Notification> findByPaymentId(Long paymentId);


    List<Notification> findByIsRead(Boolean isRead);

}