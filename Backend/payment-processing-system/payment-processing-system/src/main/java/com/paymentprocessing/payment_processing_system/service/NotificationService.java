package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.NotificationRequest;
import com.paymentprocessing.payment_processing_system.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {


    NotificationResponse createNotification(
            NotificationRequest request);


    List<NotificationResponse> getAllNotifications();


    NotificationResponse getNotificationById(Long id);


    List<NotificationResponse> getNotificationsByPaymentId(
            Long paymentId);


    void deleteNotification(Long id);

}