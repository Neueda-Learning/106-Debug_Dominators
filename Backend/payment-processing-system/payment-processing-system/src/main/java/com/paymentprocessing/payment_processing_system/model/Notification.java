package com.paymentprocessing.payment_processing_system.model;

import com.paymentprocessing.payment_processing_system.enums.DeliveryChannel;
import com.paymentprocessing.payment_processing_system.enums.NotificationStatus;
import com.paymentprocessing.payment_processing_system.enums.NotificationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;


@Table("notification")
public class Notification {


    @Id
    private Long notificationId;


    private Long paymentId;


    private NotificationType notificationType;


    private String notificationTitle;


    private String notificationMessage;


    private DeliveryChannel deliveryChannel;


    private NotificationStatus notificationStatus;


    private LocalDateTime sentAt;


    private LocalDateTime createdAt;


    private Boolean isRead;



    public Notification() {
    }


    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }


    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }


    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }


    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }


    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }


    public DeliveryChannel getDeliveryChannel() {
        return deliveryChannel;
    }

    public void setDeliveryChannel(DeliveryChannel deliveryChannel) {
        this.deliveryChannel = deliveryChannel;
    }


    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }


    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof Notification)) return false;

        Notification that = (Notification) o;

        return Objects.equals(notificationId, that.notificationId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(notificationId);
    }
}