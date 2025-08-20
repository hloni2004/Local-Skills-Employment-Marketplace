package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Notification;
import za.ac.cput.domain.User;
import za.ac.cput.repository.NotificationRepository;
import za.ac.cput.factory.NotificationFactory;

import java.util.List;

@Service
@Transactional
public class NotificationService implements IService<Notification, String> {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Notification create(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Notification read(String notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
    }

    @Override
    public Notification update(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Notification delete(String notificationId) {
        Notification notification = read(notificationId);
        notificationRepository.deleteById(notificationId);
        return notification;
    }

    // Business Logic Methods
    public Notification createJobAlert(User user, String message) {
        return create(NotificationFactory.createJobAlert(user, message));
    }

    public Notification createApplicationUpdate(User user, String message) {
        return create(NotificationFactory.createApplicationUpdate(user, message));
    }

    public Notification createPaymentNotification(User user, String message) {
        return create(NotificationFactory.createPaymentNotification(user, message));
    }

    public Notification createSystemNotification(User user, String message) {
        return create(NotificationFactory.createSystemNotification(user, message));
    }

    public Notification markAsRead(String notificationId) {
        Notification notification = read(notificationId);
        return update(new Notification.Builder()
                .copy(notification)
                .setStatus(Notification.NotificationStatus.READ)
                .build());
    }

    public List<Notification> markAllAsRead(User user) {
        List<Notification> unreadNotifications = findUnreadNotifications(user);
        return unreadNotifications.stream()
                .map(notification -> update(new Notification.Builder()
                        .copy(notification)
                        .setStatus(Notification.NotificationStatus.READ)
                        .build()))
                .toList();
    }

    public List<Notification> findNotificationsByUser(User user) {
        return notificationRepository.findByUserOrderByDateSentDesc(user);
    }

    public List<Notification> findUnreadNotifications(User user) {
        return notificationRepository.findUnreadByUser(user);
    }

    public Long countUnreadNotifications(User user) {
        return notificationRepository.countUnreadByUser(user);
    }

    public List<Notification> findNotificationsByType(Notification.NotificationType type) {
        return notificationRepository.findByType(type);
    }
}