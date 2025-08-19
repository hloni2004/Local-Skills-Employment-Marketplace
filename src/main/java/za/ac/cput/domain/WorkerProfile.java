package za.ac.cput.domain;

import jakarta.persistence.*;
import za.ac.cput.domain.*;

import java.util.List;

@Entity
@Table(name = "worker_profile")
public class WorkerProfile {
    @Id
    @Column(name = "profile_id")
    protected String profileId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    protected User user;

    @Column(name = "bio", columnDefinition = "TEXT")
    protected String bio;

    @ManyToMany
    @JoinTable(
            name = "worker_skills",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    protected List<Skill> skills;

    @Column(name = "experience")
    protected String experience;

    @Column(name = "hourly_rate")
    protected Double hourlyRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status")
    protected AvailabilityStatus availabilityStatus;

    @Column(name = "location")
    protected String location;

    @Column(name = "rating")
    protected Double rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    protected VerificationStatus verificationStatus;

    @Column(name = "verification_code")
    protected String verificationCode;

    protected WorkerProfile() {}

    public WorkerProfile(Builder builder) {
        this.profileId = builder.profileId;
        this.user = builder.user;
        this.bio = builder.bio;
        this.skills = builder.skills;
        this.experience = builder.experience;
        this.hourlyRate = builder.hourlyRate;
        this.availabilityStatus = builder.availabilityStatus;
        this.location = builder.location;
        this.rating = builder.rating;
        this.verificationStatus = builder.verificationStatus;
        this.verificationCode = builder.verificationCode;
    }

    // Getters
    public String getProfileId() { return profileId; }
    public User getUser() { return user; }
    public String getBio() { return bio; }
    public List<Skill> getSkills() { return skills; }
    public String getExperience() { return experience; }
    public Double getHourlyRate() { return hourlyRate; }
    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }
    public String getLocation() { return location; }
    public Double getRating() { return rating; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public String getVerificationCode() { return verificationCode; }

    // Enums
    public enum AvailabilityStatus { AVAILABLE, BUSY, OFFLINE }
    public enum VerificationStatus { PENDING, VERIFIED, REJECTED }

    @Override
    public String toString() {
        return "WorkerProfile{" +
                "profileId='" + profileId + '\'' +
                ", user=" + user.getUserId() +
                ", bio='" + bio + '\'' +
                ", skills=" + skills +
                ", experience='" + experience + '\'' +
                ", hourlyRate=" + hourlyRate +
                ", availabilityStatus=" + availabilityStatus +
                ", location='" + location + '\'' +
                ", rating=" + rating +
                ", verificationStatus=" + verificationStatus +
                ", verificationCode='" + verificationCode + '\'' +
                '}';
    }

    public static class Builder {
        private String profileId;
        private User user;
        private String bio;
        private List<Skill> skills;
        private String experience;
        private Double hourlyRate;
        private AvailabilityStatus availabilityStatus;
        private String location;
        private Double rating;
        private VerificationStatus verificationStatus;
        private String verificationCode;

        public Builder setProfileId(String profileId) { this.profileId = profileId; return this; }
        public Builder setUser(User user) { this.user = user; return this; }
        public Builder setBio(String bio) { this.bio = bio; return this; }
        public Builder setSkills(List<Skill> skills) { this.skills = skills; return this; }
        public Builder setExperience(String experience) { this.experience = experience; return this; }
        public Builder setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; return this; }
        public Builder setAvailabilityStatus(AvailabilityStatus availabilityStatus) { this.availabilityStatus = availabilityStatus; return this; }
        public Builder setLocation(String location) { this.location = location; return this; }
        public Builder setRating(Double rating) { this.rating = rating; return this; }
        public Builder setVerificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; return this; }
        public Builder setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; return this; }

        public Builder copy(WorkerProfile workerProfile) {
            this.profileId = workerProfile.profileId;
            this.user = workerProfile.user;
            this.bio = workerProfile.bio;
            this.skills = workerProfile.skills;
            this.experience = workerProfile.experience;
            this.hourlyRate = workerProfile.hourlyRate;
            this.availabilityStatus = workerProfile.availabilityStatus;
            this.location = workerProfile.location;
            this.rating = workerProfile.rating;
            this.verificationStatus = workerProfile.verificationStatus;
            this.verificationCode = workerProfile.verificationCode;
            return this;
        }

        public WorkerProfile build() { return new WorkerProfile(this); }
    }
}