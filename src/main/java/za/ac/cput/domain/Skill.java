package za.ac.cput.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "skill")
public class Skill {
    @Id
    @Column(name = "skill_id")
    protected String skillId;

    @Column(name = "name", nullable = false)
    protected String name;

    @Column(name = "category")
    protected String category;

    @Column(name = "description", columnDefinition = "TEXT")
    protected String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    protected VerificationStatus verificationStatus;

    protected Skill() {}

    public Skill(Builder builder) {
        this.skillId = builder.skillId;
        this.name = builder.name;
        this.category = builder.category;
        this.description = builder.description;
        this.verificationStatus = builder.verificationStatus;
    }

    // Getters
    public String getSkillId() { return skillId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }

    // Enums
    public enum VerificationStatus { PENDING, VERIFIED, REJECTED }

    @Override
    public String toString() {
        return "Skill{" +
                "skillId='" + skillId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", verificationStatus=" + verificationStatus +
                '}';
    }

    public static class Builder {
        private String skillId;
        private String name;
        private String category;
        private String description;
        private VerificationStatus verificationStatus;

        public Builder setSkillId(String skillId) { this.skillId = skillId; return this; }
        public Builder setName(String name) { this.name = name; return this; }
        public Builder setCategory(String category) { this.category = category; return this; }
        public Builder setDescription(String description) { this.description = description; return this; }
        public Builder setVerificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; return this; }

        public Builder copy(Skill skill) {
            this.skillId = skill.skillId;
            this.name = skill.name;
            this.category = skill.category;
            this.description = skill.description;
            this.verificationStatus = skill.verificationStatus;
            return this;
        }

        public Skill build() { return new Skill(this); }
    }
}