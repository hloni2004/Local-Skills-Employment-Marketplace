package za.ac.cput.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @Column(name = "admin_id")
    protected String adminId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    protected User user;

    @ElementCollection
    @CollectionTable(name = "admin_permissions", joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    protected List<Permission> permissions;

    @Column(name = "actions_log", columnDefinition = "TEXT")
    protected String actionsLog;

    protected Admin() {}

    public Admin(Builder builder) {
        this.adminId = builder.adminId;
        this.user = builder.user;
        this.permissions = builder.permissions;
        this.actionsLog = builder.actionsLog;
    }

    // Getters
    public String getAdminId() { return adminId; }
    public User getUser() { return user; }
    public List<Permission> getPermissions() { return permissions; }
    public String getActionsLog() { return actionsLog; }

    // Enums
    public enum Permission { MANAGE_USERS, VERIFY_SKILLS, RESOLVE_DISPUTES }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", user=" + user.getUserId() +
                ", permissions=" + permissions +
                ", actionsLog='" + actionsLog + '\'' +
                '}';
    }

    public static class Builder {
        private String adminId;
        private User user;
        private List<Permission> permissions;
        private String actionsLog;

        public Builder setAdminId(String adminId) { this.adminId = adminId; return this; }
        public Builder setUser(User user) { this.user = user; return this; }
        public Builder setPermissions(List<Permission> permissions) { this.permissions = permissions; return this; }
        public Builder setActionsLog(String actionsLog) { this.actionsLog = actionsLog; return this; }

        public Builder copy(Admin admin) {
            this.adminId = admin.adminId;
            this.user = admin.user;
            this.permissions = admin.permissions;
            this.actionsLog = admin.actionsLog;
            return this;
        }

        public Admin build() { return new Admin(this); }
    }
}