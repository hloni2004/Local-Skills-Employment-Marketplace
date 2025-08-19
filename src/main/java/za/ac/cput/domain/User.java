package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_id")
    protected String userId;

    @Column(name = "first_name", nullable = false)
    protected String firstName;

    @Column(name = "last_name", nullable = false)
    protected String lastName;

    @Column(name = "email", nullable = false, unique = true)
    protected String email;

    @Column(name = "password", nullable = false)
    protected String password;

    @Column(name = "phone_number")
    protected String phoneNumber;

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    protected List<Role> roles;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_mode")
    protected Mode currentMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected Status status;

    @Column(name = "date_joined")
    protected LocalDateTime dateJoined;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    protected WorkerProfile workerProfile;

    @OneToMany(mappedBy = "client")
    protected List<Job> postedJobs;

    @OneToMany(mappedBy = "worker")
    protected List<Application> applications;

    @OneToMany(mappedBy = "client")
    protected List<Contract> clientContracts;

    @OneToMany(mappedBy = "worker")
    protected List<Contract> workerContracts;

    @OneToMany(mappedBy = "reviewer")
    protected List<Review> reviewsGiven;

    @OneToMany(mappedBy = "reviewed")
    protected List<Review> reviewsReceived;

    @OneToMany(mappedBy = "user")
    protected List<Notification> notifications;

    @OneToMany(mappedBy = "user")
    protected List<Verification> verifications;

    protected User() {}

    public User(Builder builder) {
        this.userId = builder.userId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.password = builder.password;
        this.phoneNumber = builder.phoneNumber;
        this.roles = builder.roles;
        this.currentMode = builder.currentMode;
        this.status = builder.status;
        this.dateJoined = builder.dateJoined;
        this.workerProfile = builder.workerProfile;
        this.postedJobs = builder.postedJobs;
        this.applications = builder.applications;
        this.clientContracts = builder.clientContracts;
        this.workerContracts = builder.workerContracts;
        this.reviewsGiven = builder.reviewsGiven;
        this.reviewsReceived = builder.reviewsReceived;
        this.notifications = builder.notifications;
        this.verifications = builder.verifications;
    }

    // Getters for all fields
    public String getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return phoneNumber; }
    public List<Role> getRoles() { return roles; }
    public Mode getCurrentMode() { return currentMode; }
    public Status getStatus() { return status; }
    public LocalDateTime getDateJoined() { return dateJoined; }
    public WorkerProfile getWorkerProfile() { return workerProfile; }
    public List<Job> getPostedJobs() { return postedJobs; }
    public List<Application> getApplications() { return applications; }
    public List<Contract> getClientContracts() { return clientContracts; }
    public List<Contract> getWorkerContracts() { return workerContracts; }
    public List<Review> getReviewsGiven() { return reviewsGiven; }
    public List<Review> getReviewsReceived() { return reviewsReceived; }
    public List<Notification> getNotifications() { return notifications; }
    public List<Verification> getVerifications() { return verifications; }

    // Enums for Role, Mode, and Status
    public enum Role {
        CLIENT, WORKER, BOTH
    }

    public enum Mode {
        CLIENT, WORKER
    }

    public enum Status {
        ACTIVE, SUSPENDED, DEACTIVATED
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", roles=" + roles +
                ", currentMode=" + currentMode +
                ", status=" + status +
                ", dateJoined=" + dateJoined +
                '}';
    }

    public static class Builder {
        private String userId;
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String phoneNumber;
        private List<Role> roles;
        private Mode currentMode;
        private Status status;
        private LocalDateTime dateJoined;
        private WorkerProfile workerProfile;
        private List<Job> postedJobs;
        private List<Application> applications;
        private List<Contract> clientContracts;
        private List<Contract> workerContracts;
        private List<Review> reviewsGiven;
        private List<Review> reviewsReceived;
        private List<Notification> notifications;
        private List<Verification> verifications;

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setRoles(List<Role> roles) {
            this.roles = roles;
            return this;
        }

        public Builder setCurrentMode(Mode currentMode) {
            this.currentMode = currentMode;
            return this;
        }

        public Builder setStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder setDateJoined(LocalDateTime dateJoined) {
            this.dateJoined = dateJoined;
            return this;
        }

        public Builder setWorkerProfile(WorkerProfile workerProfile) {
            this.workerProfile = workerProfile;
            return this;
        }

        public Builder setPostedJobs(List<Job> postedJobs) {
            this.postedJobs = postedJobs;
            return this;
        }

        public Builder setApplications(List<Application> applications) {
            this.applications = applications;
            return this;
        }

        public Builder setClientContracts(List<Contract> clientContracts) {
            this.clientContracts = clientContracts;
            return this;
        }

        public Builder setWorkerContracts(List<Contract> workerContracts) {
            this.workerContracts = workerContracts;
            return this;
        }

        public Builder setReviewsGiven(List<Review> reviewsGiven) {
            this.reviewsGiven = reviewsGiven;
            return this;
        }

        public Builder setReviewsReceived(List<Review> reviewsReceived) {
            this.reviewsReceived = reviewsReceived;
            return this;
        }

        public Builder setNotifications(List<Notification> notifications) {
            this.notifications = notifications;
            return this;
        }

        public Builder setVerifications(List<Verification> verifications) {
            this.verifications = verifications;
            return this;
        }

        public Builder copy(User user) {
            this.userId = user.userId;
            this.firstName = user.firstName;
            this.lastName = user.lastName;
            this.email = user.email;
            this.password = user.password;
            this.phoneNumber = user.phoneNumber;
            this.roles = user.roles;
            this.currentMode = user.currentMode;
            this.status = user.status;
            this.dateJoined = user.dateJoined;
            this.workerProfile = user.workerProfile;
            this.postedJobs = user.postedJobs;
            this.applications = user.applications;
            this.clientContracts = user.clientContracts;
            this.workerContracts = user.workerContracts;
            this.reviewsGiven = user.reviewsGiven;
            this.reviewsReceived = user.reviewsReceived;
            this.notifications = user.notifications;
            this.verifications = user.verifications;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}