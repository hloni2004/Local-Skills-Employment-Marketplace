package za.ac.cput.factory;

import za.ac.cput.domain.*;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;

public class NotificationFactory {

    public static Notification createNotification(User user, String message,
                                                  Notification.NotificationType type) {

        // Validate inputs
        if (user == null) {
            return null;
        }
        if (ValidationHelper.isNullOrEmpty(message)) {
            return null;
        }
        if (type == null) {
            return null;
        }

        return new Notification.Builder()
                .setNotificationId(ValidationHelper.generateId())
                .setUser(user)
                .setMessage(message.trim())
                .setType(type)
                .setStatus(Notification.NotificationStatus.UNREAD)
                .setDateSent(LocalDateTime.now())
                .build();
    }

    public static Notification createJobAlert(User user, String message) {
        return createNotification(user, message, Notification.NotificationType.JOB_ALERT);
    }

    public static Notification createApplicationUpdate(User user, String message) {
        return createNotification(user, message, Notification.NotificationType.APPLICATION_UPDATE);
    }

    public static Notification createPaymentNotification(User user, String message) {
        return createNotification(user, message, Notification.NotificationType.PAYMENT);
    }

    public static Notification createSystemNotification(User user, String message) {
        return createNotification(user, message, Notification.NotificationType.SYSTEM);
    }
}