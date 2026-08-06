package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.NotificationRequest;
import com.paymentprocessing.payment_processing_system.dto.NotificationResponse;
import com.paymentprocessing.payment_processing_system.enums.DeliveryChannel;
import com.paymentprocessing.payment_processing_system.enums.NotificationStatus;
import com.paymentprocessing.payment_processing_system.enums.NotificationType;
import com.paymentprocessing.payment_processing_system.exception.NotificationException;
import com.paymentprocessing.payment_processing_system.model.Notification;
import com.paymentprocessing.payment_processing_system.repository.NotificationRepository;
import com.paymentprocessing.payment_processing_system.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void createNotification_shouldCreateNotificationAndSaveToRepository() {
        NotificationRequest request = buildNotificationRequest();

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setNotificationId(1L);
            return notification;
        });

        NotificationResponse response = notificationService.createNotification(request);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification savedEntity = notificationCaptor.getValue();
        assertThat(savedEntity.getNotificationId()).isEqualTo(1L);
        assertThat(savedEntity.getPaymentId()).isEqualTo(request.getPaymentId());
        assertThat(savedEntity.getNotificationType()).isEqualTo(request.getNotificationType());
        assertThat(savedEntity.getNotificationTitle()).isEqualTo(request.getNotificationTitle());
        assertThat(savedEntity.getNotificationMessage()).isEqualTo(request.getNotificationMessage());
        assertThat(savedEntity.getDeliveryChannel()).isEqualTo(request.getDeliveryChannel());
        assertThat(savedEntity.getNotificationStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(savedEntity.getIsRead()).isFalse();
        assertThat(savedEntity.getCreatedAt()).isNotNull();

        assertThat(response.getNotificationId()).isEqualTo(1L);
        assertThat(response.getPaymentId()).isEqualTo(request.getPaymentId());
        assertThat(response.getNotificationType()).isEqualTo(request.getNotificationType());
        assertThat(response.getNotificationTitle()).isEqualTo(request.getNotificationTitle());
        assertThat(response.getNotificationMessage()).isEqualTo(request.getNotificationMessage());
        assertThat(response.getDeliveryChannel()).isEqualTo(request.getDeliveryChannel());
        assertThat(response.getNotificationStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(response.getIsRead()).isFalse();
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    void getNotificationById_shouldReturnNotificationWhenExists() {
        Long notificationId = 10L;
        Notification notification = buildNotificationEntity(notificationId);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        NotificationResponse response = notificationService.getNotificationById(notificationId);

        verify(notificationRepository).findById(notificationId);
        assertThat(response.getNotificationId()).isEqualTo(notification.getNotificationId());
        assertThat(response.getPaymentId()).isEqualTo(notification.getPaymentId());
        assertThat(response.getNotificationType()).isEqualTo(notification.getNotificationType());
        assertThat(response.getNotificationStatus()).isEqualTo(notification.getNotificationStatus());
    }

    @Test
    void getNotificationById_whenNotFound_shouldThrowNotificationException() {
        Long notificationId = 99L;
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        NotificationException exception = assertThrows(
                NotificationException.class,
                () -> notificationService.getNotificationById(notificationId)
        );

        verify(notificationRepository).findById(notificationId);
        assertThat(exception.getMessage()).isEqualTo("Notification not found with id: 99");
    }

    @Test
    void deleteNotification_shouldDeleteNotificationWhenExists() {
        Long notificationId = 5L;
        when(notificationRepository.existsById(notificationId)).thenReturn(true);

        notificationService.deleteNotification(notificationId);

        verify(notificationRepository).existsById(notificationId);
        verify(notificationRepository).deleteById(notificationId);
    }

    @Test
    void deleteNotification_whenNotFound_shouldThrowNotificationException() {
        Long notificationId = 6L;
        when(notificationRepository.existsById(notificationId)).thenReturn(false);

        NotificationException exception = assertThrows(
                NotificationException.class,
                () -> notificationService.deleteNotification(notificationId)
        );

        verify(notificationRepository).existsById(notificationId);
        verify(notificationRepository, never()).deleteById(notificationId);
        assertThat(exception.getMessage()).isEqualTo("Notification not found with id: 6");
    }

    @Test
    void createNotification_whenRepositorySaveFails_shouldPropagateException() {
        NotificationRequest request = buildNotificationRequest();
        when(notificationRepository.save(any(Notification.class)))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> notificationService.createNotification(request)
        );

        verify(notificationRepository).save(any(Notification.class));
        assertThat(exception.getMessage()).isEqualTo("Database error");
    }

    private NotificationRequest buildNotificationRequest() {
        NotificationRequest request = new NotificationRequest();
        request.setPaymentId(1001L);
        request.setNotificationType(NotificationType.PAYMENT_SUCCESS);
        request.setNotificationTitle("Payment Successful");
        request.setNotificationMessage("Your payment has been completed");
        request.setDeliveryChannel(DeliveryChannel.EMAIL);
        return request;
    }

    private Notification buildNotificationEntity(Long id) {
        Notification notification = new Notification();
        notification.setNotificationId(id);
        notification.setPaymentId(1001L);
        notification.setNotificationType(NotificationType.PAYMENT_SUCCESS);
        notification.setNotificationTitle("Payment Successful");
        notification.setNotificationMessage("Your payment has been completed");
        notification.setDeliveryChannel(DeliveryChannel.EMAIL);
        notification.setNotificationStatus(NotificationStatus.PENDING);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSentAt(null);
        return notification;
    }
}

