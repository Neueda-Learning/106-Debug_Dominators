package com.paymentprocessing.payment_processing_system.controller;


import com.paymentprocessing.payment_processing_system.dto.NotificationRequest;
import com.paymentprocessing.payment_processing_system.dto.NotificationResponse;
import com.paymentprocessing.payment_processing_system.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification APIs", description = "Operations for creating and tracking payment-related notifications")
public class NotificationController {


    private final NotificationService notificationService;


    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }




    @PostMapping
    @Operation(
            summary = "Create Notification",
            description = "Creates a new notification linked to a payment event"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notification created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or validation failure"),
            @ApiResponse(responseCode = "404", description = "Referenced payment resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationResponse> createNotification(
            @RequestBody NotificationRequest request) {


        return new ResponseEntity<>(
                notificationService.createNotification(request),
                HttpStatus.CREATED
        );
    }




    @GetMapping
    @Operation(
            summary = "Get All Notifications",
            description = "Retrieves all notification records"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Notification resources not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<NotificationResponse>>
    getAllNotifications() {


        return ResponseEntity.ok(
                notificationService.getAllNotifications()
        );
    }




    @GetMapping("/{id}")
    @Operation(
            summary = "Get Notification By Id",
            description = "Retrieves a notification by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationResponse>
    getNotificationById(
            @PathVariable Long id) {


        return ResponseEntity.ok(
                notificationService.getNotificationById(id)
        );
    }




    @GetMapping("/payment/{paymentId}")
    @Operation(
            summary = "Get Notifications By Payment Id",
            description = "Retrieves all notifications associated with a specific payment id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Payment or notifications not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<NotificationResponse>>
    getByPaymentId(
            @PathVariable Long paymentId) {


        return ResponseEntity.ok(
                notificationService
                        .getNotificationsByPaymentId(paymentId)
        );
    }




    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Notification",
            description = "Deletes a notification by id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> deleteNotification(
            @PathVariable Long id) {


        notificationService.deleteNotification(id);


        return ResponseEntity.ok(
                "Notification deleted successfully"
        );
    }
}
