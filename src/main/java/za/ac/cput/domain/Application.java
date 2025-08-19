package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "application")
public class Application {
    @Id
    @Column(name = "application_id")
    protected String applicationId;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    protected Job job;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    protected User worker;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    protected String coverLetter;

    @Column(name = "expected_pay")
    protected Double expectedPay;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected ApplicationStatus status;

    @Column(name = "date_applied")
    protected LocalDateTime dateApplied;

    protected Application() {}

    public Application(Builder builder) {
        this.applicationId = builder.applicationId;
        this.job = builder.job;
        this.worker = builder.worker;
        this.coverLetter = builder.coverLetter;
        this.expectedPay = builder.expectedPay;
        this.status = builder.status;
        this.dateApplied = builder.dateApplied;
    }

    // Getters
    public String getApplicationId() { return applicationId; }
    public Job getJob() { return job; }
    public User getWorker() { return worker; }
    public String getCoverLetter() { return coverLetter; }
    public Double getExpectedPay() { return expectedPay; }
    public ApplicationStatus getStatus() { return status; }
    public LocalDateTime getDateApplied() { return dateApplied; }

    // Enums
    public enum ApplicationStatus { PENDING, ACCEPTED, REJECTED, WITHDRAWN }

    @Override
    public String toString() {
        return "Application{" +
                "applicationId='" + applicationId + '\'' +
                ", job=" + job.getJobId() +
                ", worker=" + worker.getUserId() +
                ", coverLetter='" + coverLetter + '\'' +
                ", expectedPay=" + expectedPay +
                ", status=" + status +
                ", dateApplied=" + dateApplied +
                '}';
    }

    public static class Builder {
        private String applicationId;
        private Job job;
        private User worker;
        private String coverLetter;
        private Double expectedPay;
        private ApplicationStatus status;
        private LocalDateTime dateApplied;

        public Builder setApplicationId(String applicationId) { this.applicationId = applicationId; return this; }
        public Builder setJob(Job job) { this.job = job; return this; }
        public Builder setWorker(User worker) { this.worker = worker; return this; }
        public Builder setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; return this; }
        public Builder setExpectedPay(Double expectedPay) { this.expectedPay = expectedPay; return this; }
        public Builder setStatus(ApplicationStatus status) { this.status = status; return this; }
        public Builder setDateApplied(LocalDateTime dateApplied) { this.dateApplied = dateApplied; return this; }

        public Builder copy(Application application) {
            this.applicationId = application.applicationId;
            this.job = application.job;
            this.worker = application.worker;
            this.coverLetter = application.coverLetter;
            this.expectedPay = application.expectedPay;
            this.status = application.status;
            this.dateApplied = application.dateApplied;
            return this;
        }

        public Application build() { return new Application(this); }
    }
}