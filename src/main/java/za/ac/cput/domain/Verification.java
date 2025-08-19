package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification")
public class Verification {
    @Id
    @Column(name = "verification_id")
    protected String verificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    protected User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    protected DocumentType documentType;

    @Column(name = "document_url")
    protected String documentUrl;

    @Column(name = "verification_code")
    protected String verificationCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected VerificationStatus status;

    @Column(name = "date_submitted")
    protected LocalDateTime dateSubmitted;

    @Column(name = "date_verified")
    protected LocalDateTime dateVerified;

    protected Verification() {}

    public Verification(Builder builder) {
        this.verificationId = builder.verificationId;
        this.user = builder.user;
        this.documentType = builder.documentType;
        this.documentUrl = builder.documentUrl;
        this.verificationCode = builder.verificationCode;
        this.status = builder.status;
        this.dateSubmitted = builder.dateSubmitted;
        this.dateVerified = builder.dateVerified;
    }

    // Getters
    public String getVerificationId() { return verificationId; }
    public User getUser() { return user; }
    public DocumentType getDocumentType() { return documentType; }
    public String getDocumentUrl() { return documentUrl; }
    public String getVerificationCode() { return verificationCode; }
    public VerificationStatus getStatus() { return status; }
    public LocalDateTime getDateSubmitted() { return dateSubmitted; }
    public LocalDateTime getDateVerified() { return dateVerified; }

    // Enums
    public enum DocumentType { ID, CERTIFICATE, LICENSE }
    public enum VerificationStatus { PENDING, VERIFIED, REJECTED }

    @Override
    public String toString() {
        return "Verification{" +
                "verificationId='" + verificationId + '\'' +
                ", user=" + user.getUserId() +
                ", documentType=" + documentType +
                ", documentUrl='" + documentUrl + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                ", status="+ status +
                ", dateSubmitted=" + dateSubmitted +
                ", dateVerified=" + dateVerified +
                '}';
    }

    public static class Builder {
        private String verificationId;
        private User user;
        private DocumentType documentType;
        private String documentUrl;
        private String verificationCode;
        private VerificationStatus status;
        private LocalDateTime dateSubmitted;
        private LocalDateTime dateVerified;

        public Builder setVerificationId(String verificationId) { this.verificationId = verificationId; return this; }
        public Builder setUser(User user) { this.user = user; return this; }
        public Builder setDocumentType(DocumentType documentType) { this.documentType = documentType; return this; }
        public Builder setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; return this; }
        public Builder setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; return this; }
        public Builder setStatus(VerificationStatus status) { this.status = status; return this; }
        public Builder setDateSubmitted(LocalDateTime dateSubmitted) { this.dateSubmitted = dateSubmitted; return this; }
        public Builder setDateVerified(LocalDateTime dateVerified) { this.dateVerified = dateVerified; return this; }

        public Builder copy(Verification verification) {
            this.verificationId = verification.verificationId;
            this.user = verification.user;
            this.documentType = verification.documentType;
            this.documentUrl = verification.documentUrl;
            this.verificationCode = verification.verificationCode;
            this.status = verification.status;
            this.dateSubmitted = verification.dateSubmitted;
            this.dateVerified = verification.dateVerified;
            return this;
        }

        public Verification build() { return new Verification(this); }
    }
}