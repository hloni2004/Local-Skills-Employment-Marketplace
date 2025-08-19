package za.ac.cput.domain;

import jakarta.persistence.*;
import za.ac.cput.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "job")
public class Job {
    @Id
    @Column(name = "job_id")
    protected String jobId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    protected User client;

    @Column(name = "title", nullable = false)
    protected String title;

    @Column(name = "description", columnDefinition = "TEXT")
    protected String description;

    @Column(name = "category")
    protected String category;

    @Column(name = "budget")
    protected Double budget;

    @Column(name = "location")
    protected String location;

    @Column(name = "date_posted")
    protected LocalDateTime datePosted;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected JobStatus status;

    protected Job() {}

    public Job(Builder builder) {
        this.jobId = builder.jobId;
        this.client = builder.client;
        this.title = builder.title;
        this.description = builder.description;
        this.category = builder.category;
        this.budget = builder.budget;
        this.location = builder.location;
        this.datePosted = builder.datePosted;
        this.status = builder.status;
    }

    // Getters
    public String getJobId() { return jobId; }
    public User getClient() { return client; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public Double getBudget() { return budget; }
    public String getLocation() { return location; }
    public LocalDateTime getDatePosted() { return datePosted; }
    public JobStatus getStatus() { return status; }

    // Enums
    public enum JobStatus { OPEN, ASSIGNED, COMPLETED, CANCELLED }

    @Override
    public String toString() {
        return "Job{" +
                "jobId='" + jobId + '\'' +
                ", client=" + client.getUserId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", budget=" + budget +
                ", location='" + location + '\'' +
                ", datePosted=" + datePosted +
                ", status=" + status +
                '}';
    }

    public static class Builder {
        private String jobId;
        private User client;
        private String title;
        private String description;
        private String category;
        private Double budget;
        private String location;
        private LocalDateTime datePosted;
        private JobStatus status;

        public Builder setJobId(String jobId) { this.jobId = jobId; return this; }
        public Builder setClient(User client) { this.client = client; return this; }
        public Builder setTitle(String title) { this.title = title; return this; }
        public Builder setDescription(String description) { this.description = description; return this; }
        public Builder setCategory(String category) { this.category = category; return this; }
        public Builder setBudget(Double budget) { this.budget = budget; return this; }
        public Builder setLocation(String location) { this.location = location; return this; }
        public Builder setDatePosted(LocalDateTime datePosted) { this.datePosted = datePosted; return this; }
        public Builder setStatus(JobStatus status) { this.status = status; return this; }

        public Builder copy(Job job) {
            this.jobId = job.jobId;
            this.client = job.client;
            this.title = job.title;
            this.description = job.description;
            this.category = job.category;
            this.budget = job.budget;
            this.location = job.location;
            this.datePosted = job.datePosted;
            this.status = job.status;
            return this;
        }

        public Job build() { return new Job(this); }
    }
}