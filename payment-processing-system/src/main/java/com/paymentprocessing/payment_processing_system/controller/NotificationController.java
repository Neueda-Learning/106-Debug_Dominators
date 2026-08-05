package com.paymentprocessing.payment_processing_system.controller;


import com.paymentprocessing.payment_processing_system.dto.NotificationRequest;
import com.paymentprocessing.payment_processing_system.dto.NotificationResponse;
import com.paymentprocessing.payment_processing_system.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/notifications")
public class NotificationController {


    private final NotificationService notificationService;


    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }




    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @RequestBody NotificationRequest request) {


        return new ResponseEntity<>(
                notificationService.createNotification(request),
                HttpStatus.CREATED
        );
    }




    @GetMapping
    public ResponseEntity<List<NotificationResponse>>
    getAllNotifications() {


        return ResponseEntity.ok(
                notificationService.getAllNotifications()
        );
    }




    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse>
    getNotificationById(
            @PathVariable Long id) {


        return ResponseEntity.ok(
                notificationService.getNotificationById(id)
        );
    }




    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<NotificationResponse>>
    getByPaymentId(
            @PathVariable Long paymentId) {


        return ResponseEntity.ok(
                notificationService
                        .getNotificationsByPaymentId(paymentId)
        );
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(
            @PathVariable Long id) {


        notificationService.deleteNotification(id);


        return ResponseEntity.ok(
                "Notification deleted successfully"
        );
    }
}