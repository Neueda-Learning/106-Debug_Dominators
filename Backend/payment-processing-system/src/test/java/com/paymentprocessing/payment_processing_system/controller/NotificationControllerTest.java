package com.paymentprocessing.payment_processing_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentprocessing.payment_processing_system.dto.NotificationRequest;
import com.paymentprocessing.payment_processing_system.dto.NotificationResponse;
import com.paymentprocessing.payment_processing_system.enums.DeliveryChannel;
import com.paymentprocessing.payment_processing_system.enums.NotificationStatus;
import com.paymentprocessing.payment_processing_system.enums.NotificationType;
import com.paymentprocessing.payment_processing_system.exception.GlobalExceptionHandler;
import com.paymentprocessing.payment_processing_system.exception.NotificationException;
import com.paymentprocessing.payment_processing_system.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createNotification_shouldReturnCreated() throws Exception {
        NotificationRequest request = buildRequest();
        NotificationResponse response = buildResponse(1L);

        when(notificationService.createNotification(any(NotificationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notificationId").value(1L));
    }

    @Test
    void getAllNotifications_shouldReturnOk() throws Exception {
        when(notificationService.getAllNotifications()).thenReturn(List.of(buildResponse(1L)));

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationId").value(1L));
    }

    @Test
    void getNotificationById_shouldReturnOk() throws Exception {
        when(notificationService.getNotificationById(1L)).thenReturn(buildResponse(1L));

        mockMvc.perform(get("/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").value(1L));
    }

    @Test
    void getNotificationById_whenNotFound_shouldReturnBadRequest() throws Exception {
        when(notificationService.getNotificationById(99L))
                .thenThrow(new NotificationException("Notification not found with id: 99"));

        mockMvc.perform(get("/notifications/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BUSINESS_ERROR"));
    }

    @Test
    void deleteNotification_shouldReturnOk() throws Exception {
        doNothing().when(notificationService).deleteNotification(1L);

        mockMvc.perform(delete("/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Notification deleted successfully"));
    }

    private NotificationRequest buildRequest() {
        NotificationRequest request = new NotificationRequest();
        request.setPaymentId(1001L);
        request.setNotificationType(NotificationType.PAYMENT_SUCCESS);
        request.setNotificationTitle("Payment Successful");
        request.setNotificationMessage("Your payment has been completed");
        request.setDeliveryChannel(DeliveryChannel.EMAIL);
        return request;
    }

    private NotificationResponse buildResponse(Long id) {
        NotificationResponse response = new NotificationResponse();
        response.setNotificationId(id);
        response.setPaymentId(1001L);
        response.setNotificationType(NotificationType.PAYMENT_SUCCESS);
        response.setNotificationTitle("Payment Successful");
        response.setNotificationMessage("Your payment has been completed");
        response.setDeliveryChannel(DeliveryChannel.EMAIL);
        response.setNotificationStatus(NotificationStatus.PENDING);
        response.setCreatedAt(LocalDateTime.now());
        response.setSentAt(null);
        response.setIsRead(false);
        return response;
    }
}
