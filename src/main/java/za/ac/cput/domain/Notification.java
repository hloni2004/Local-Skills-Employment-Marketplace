package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @Column(name = "notification_id")
    protected String notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    protected User user;

    @Column(name = "message", columnDefinition = "TEXT")
    protected String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    protected NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected NotificationStatus status;

    @Column(name = "date_sent")
    protected LocalDateTime dateSent;

    protected Notification() {}

    public Notification(Builder builder) {
        this.notificationId = builder.notificationId;
        this.user = builder.user;
        this.message = builder.message;
        this.type = builder.type;
        this.status = builder.status;
        this.dateSent = builder.dateSent;
    }

    // Getters
    public String getNotificationId() { return notificationId; }
    public User getUser() { return user; }
    public String getMessage() { return message; }
    public NotificationType getType() { return type; }
    public NotificationStatus getStatus() { return status; }
    public LocalDateTime getDateSent() { return dateSent; }

    // Enums
    public enum NotificationType { JOB_ALERT, APPLICATION_UPDATE, PAYMENT, SYSTEM }
    public enum NotificationStatus { UNREAD, READ }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", user=" + user.getUserId() +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", dateSent=" + dateSent +
                '}';
    }

    public static class Builder {
        private String notificationId;
        private User user;
        private String message;
        private NotificationType type;
        private NotificationStatus status;
        private LocalDateTime dateSent;

        public Builder setNotificationId(String notificationId) { this.notificationId = notificationId; return this; }
        public Builder setUser(User user) { this.user = user; return this; }
        public Builder setMessage(String message) { this.message = message; return this; }
        public Builder setType(NotificationType type) { this.type = type; return this; }
        public Builder setStatus(NotificationStatus status) { this.status = status; return this; }
        public Builder setDateSent(LocalDateTime dateSent) { this.dateSent = dateSent; return this; }

        public Builder copy(Notification notification) {
            this.notificationId = notification.notificationId;
            this.user = notification.user;
            this.message = notification.message;
            this.type = notification.type;
            this.status = notification.status;
            this.dateSent = notification.dateSent;
             return this;
        }

        public Notification build() { return new Notification(this); }
    }
}