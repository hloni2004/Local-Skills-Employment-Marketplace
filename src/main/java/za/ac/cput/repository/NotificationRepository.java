package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Notification;
import za.ac.cput.domain.User;

import java.util.List;

// ========================= NOTIFICATION REPOSITORY =========================
@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    // Find notifications by user
    List<Notification> findByUser(User user);

    // Find notifications by type
    List<Notification> findByType(Notification.NotificationType type);

    // Find notifications by status
    List<Notification> findByStatus(Notification.NotificationStatus status);

    // Find unread notifications for user
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.status = 'UNREAD' ORDER BY n.dateSent DESC")
    List<Notification> findUnreadByUser(@Param("user") User user);

    // Find notifications by user and type
    List<Notification> findByUserAndType(User user, Notification.NotificationType type);

    // Count unread notifications for user
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.status = 'UNREAD'")
    Long countUnreadByUser(@Param("user") User user);

    // Find recent notifications for user
    @Query("SELECT n FROM Notification n WHERE n.user = :user ORDER BY n.dateSent DESC")
    List<Notification> findByUserOrderByDateSentDesc(@Param("user") User user);
}
