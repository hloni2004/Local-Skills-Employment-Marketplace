package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Notification;
import za.ac.cput.domain.User;
import static org.junit.jupiter.api.Assertions.*;

class NotificationFactoryTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFactory.createWorkerUser("Test", "User", "test@email.com", "Password123!", "0821234567");
    }

    @Test
    void createNotification() {
        String message = "This is a test notification message for the user.";
        Notification notification = NotificationFactory.createNotification(user, message,
                Notification.NotificationType.SYSTEM);

        assertNotNull(notification);
        assertEquals(user, notification.getUser());
        assertEquals(message, notification.getMessage());
        assertEquals(Notification.NotificationType.SYSTEM, notification.getType());
        assertEquals(Notification.NotificationStatus.UNREAD, notification.getStatus());
        assertNotNull(notification.getNotificationId());
        assertNotNull(notification.getDateSent());
        System.out.println(notification);
    }

    @Test
    void createNotification_InvalidUser() {
        Notification notification = NotificationFactory.createNotification(null, "Message",
                Notification.NotificationType.SYSTEM);
        assertNull(notification);
    }

    @Test
    void createNotification_InvalidMessage() {
        // Test with null message
        Notification notification = NotificationFactory.createNotification(user, null,
                Notification.NotificationType.SYSTEM);
        assertNull(notification);

        // Test with empty message
        notification = NotificationFactory.createNotification(user, "",
                Notification.NotificationType.SYSTEM);
        assertNull(notification);

        // Test with whitespace-only message
        notification = NotificationFactory.createNotification(user, "   ",
                Notification.NotificationType.SYSTEM);
        assertNull(notification);
    }

    @Test
    void createNotification_InvalidType() {
        Notification notification = NotificationFactory.createNotification(user, "Valid message", null);
        assertNull(notification);
    }

    @Test
    void createJobAlert() {
        String message = "New job matching your skills has been posted: Web Developer position.";
        Notification notification = NotificationFactory.createJobAlert(user, message);

        assertNotNull(notification);
        assertEquals(user, notification.getUser());
        assertEquals(message, notification.getMessage());
        assertEquals(Notification.NotificationType.JOB_ALERT, notification.getType());
        assertEquals(Notification.NotificationStatus.UNREAD, notification.getStatus());
        System.out.println(notification);
    }

    @Test
    void createApplicationUpdate() {
        String message = "Your application for 'Mobile App Developer' has been accepted.";
        Notification notification = NotificationFactory.createApplicationUpdate(user, message);

        assertNotNull(notification);
        assertEquals(Notification.NotificationType.APPLICATION_UPDATE, notification.getType());
        assertEquals(message, notification.getMessage());
        System.out.println(notification);
    }

    @Test
    void createPaymentNotification() {
        String message = "Payment of R5000 has been released for completed project.";
        Notification notification = NotificationFactory.createPaymentNotification(user, message);

        assertNotNull(notification);
        assertEquals(Notification.NotificationType.PAYMENT, notification.getType());
        assertEquals(message, notification.getMessage());
        System.out.println(notification);
    }

    @Test
    void createSystemNotification() {
        String message = "System maintenance scheduled for tonight from 2AM to 4AM.";
        Notification notification = NotificationFactory.createSystemNotification(user, message);

        assertNotNull(notification);
        assertEquals(Notification.NotificationType.SYSTEM, notification.getType());
        assertEquals(message, notification.getMessage());
        System.out.println(notification);
    }
}
