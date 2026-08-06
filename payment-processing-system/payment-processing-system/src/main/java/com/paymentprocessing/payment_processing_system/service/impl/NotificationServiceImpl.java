package com.paymentprocessing.payment_processing_system.service.impl;


import com.paymentprocessing.payment_processing_system.dto.NotificationRequest;
import com.paymentprocessing.payment_processing_system.dto.NotificationResponse;
import com.paymentprocessing.payment_processing_system.enums.NotificationStatus;
import com.paymentprocessing.payment_processing_system.exception.NotificationException;
import com.paymentprocessing.payment_processing_system.model.Notification;
import com.paymentprocessing.payment_processing_system.repository.NotificationRepository;
import com.paymentprocessing.payment_processing_system.service.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Service
public class NotificationServiceImpl implements NotificationService {


    private static final Logger log =
            LoggerFactory.getLogger(NotificationServiceImpl.class);



    private final NotificationRepository notificationRepository;



    public NotificationServiceImpl(
            NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }





    @Override
    public NotificationResponse createNotification(
            NotificationRequest request) {


        log.info(
                "Creating notification for payment id: {}",
                request.getPaymentId()
        );


        Notification notification = new Notification();



        notification.setPaymentId(
                request.getPaymentId()
        );


        notification.setNotificationType(
                request.getNotificationType()
        );


        notification.setNotificationTitle(
                request.getNotificationTitle()
        );


        notification.setNotificationMessage(
                request.getNotificationMessage()
        );


        notification.setDeliveryChannel(
                request.getDeliveryChannel()
        );


        notification.setNotificationStatus(
                NotificationStatus.PENDING
        );


        notification.setIsRead(false);


        notification.setCreatedAt(
                LocalDateTime.now()
        );



        Notification savedNotification =
                notificationRepository.save(notification);



        log.info(
                "Notification created successfully with id: {}",
                savedNotification.getNotificationId()
        );


        return mapToResponse(savedNotification);
    }





    @Override
    public List<NotificationResponse> getAllNotifications() {


        log.info("Fetching all notifications");


        List<NotificationResponse> responses =
                new ArrayList<>();


        notificationRepository.findAll()
                .forEach(notification ->
                        responses.add(
                                mapToResponse(notification)
                        ));


        return responses;
    }





    @Override
    public NotificationResponse getNotificationById(Long id) {


        log.info(
                "Fetching notification with id: {}",
                id
        );



        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() -> {


                            log.error(
                                    "Notification not found with id: {}",
                                    id
                            );


                            return new NotificationException(
                                    "Notification not found with id: "
                                            + id
                            );
                        });



        return mapToResponse(notification);
    }





    @Override
    public List<NotificationResponse> getNotificationsByPaymentId(
            Long paymentId) {


        log.info(
                "Fetching notifications for payment id: {}",
                paymentId
        );


        List<NotificationResponse> responses =
                new ArrayList<>();


        notificationRepository.findByPaymentId(paymentId)
                .forEach(notification ->
                        responses.add(
                                mapToResponse(notification)
                        ));


        return responses;
    }





    @Override
    public void deleteNotification(Long id) {


        log.info(
                "Deleting notification with id: {}",
                id
        );


        if (!notificationRepository.existsById(id)) {


            log.error(
                    "Notification not found for deletion with id: {}",
                    id
            );


            throw new NotificationException(
                    "Notification not found with id: " + id
            );
        }



        notificationRepository.deleteById(id);



        log.info(
                "Notification deleted successfully with id: {}",
                id
        );
    }





    private NotificationResponse mapToResponse(
            Notification notification) {


        NotificationResponse response =
                new NotificationResponse();



        response.setNotificationId(
                notification.getNotificationId()
        );


        response.setPaymentId(
                notification.getPaymentId()
        );


        response.setNotificationType(
                notification.getNotificationType()
        );


        response.setNotificationTitle(
                notification.getNotificationTitle()
        );


        response.setNotificationMessage(
                notification.getNotificationMessage()
        );


        response.setDeliveryChannel(
                notification.getDeliveryChannel()
        );


        response.setNotificationStatus(
                notification.getNotificationStatus()
        );


        response.setSentAt(
                notification.getSentAt()
        );


        response.setCreatedAt(
                notification.getCreatedAt()
        );


        response.setIsRead(
                notification.getIsRead()
        );


        return response;
    }
}