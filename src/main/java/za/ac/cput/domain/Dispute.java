package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispute")
public class Dispute {
    @Id
    @Column(name = "dispute_id")
    protected String disputeId;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    protected Contract contract;

    @ManyToOne
    @JoinColumn(name = "opened_by", nullable = false)
    protected User openedBy;

    @Column(name = "reason", columnDefinition = "TEXT")
    protected String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected DisputeStatus status;

    @Column(name = "resolution_details", columnDefinition = "TEXT")
    protected String resolutionDetails;

    @Column(name = "date_opened")
    protected LocalDateTime dateOpened;

    protected Dispute() {}

    public Dispute(Builder builder) {
        this.disputeId = builder.disputeId;
        this.contract = builder.contract;
        this.openedBy = builder.openedBy;
        this.reason = builder.reason;
        this.status = builder.status;
        this.resolutionDetails = builder.resolutionDetails;
        this.dateOpened = builder.dateOpened;
    }

    // Getters
    public String getDisputeId() { return disputeId; }
    public Contract getContract() { return contract; }
    public User getOpenedBy() { return openedBy; }
    public String getReason() { return reason; }
    public DisputeStatus getStatus() { return status; }
    public String getResolutionDetails() { return resolutionDetails; }
    public LocalDateTime getDateOpened() { return dateOpened; }

    // Enums
    public enum DisputeStatus { OPEN, RESOLVED, DISMISSED }

    @Override
    public String toString() {
        return "Dispute{" +
                "disputeId='" + disputeId + '\'' +
                ", contract=" + contract.getContractId() +
                ", openedBy=" + openedBy.getUserId() +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                ", resolutionDetails='" + resolutionDetails + '\'' +
                ", dateOpened=" + dateOpened +
                '}';
    }

    public static class Builder {
        private String disputeId;
        private Contract contract;
        private User openedBy;
        private String reason;
        private DisputeStatus status;
        private String resolutionDetails;
        private LocalDateTime dateOpened;

        public Builder setDisputeId(String disputeId) { this.disputeId = disputeId; return this; }
        public Builder setContract(Contract contract) { this.contract = contract; return this; }
        public Builder setOpenedBy(User openedBy) { this.openedBy = openedBy; return this; }
        public Builder setReason(String reason) { this.reason = reason; return this; }
        public Builder setStatus(DisputeStatus status) { this.status = status; return this; }
        public Builder setResolutionDetails(String resolutionDetails) { this.resolutionDetails = resolutionDetails; return this; }
        public Builder setDateOpened(LocalDateTime dateOpened) { this.dateOpened = dateOpened; return this; }

        public Builder copy(Dispute dispute) {
            this.disputeId = dispute.disputeId;
            this.contract = dispute.contract;
            this.openedBy = dispute.openedBy;
            this.reason = dispute.reason;
            this.status = dispute.status;
            this.resolutionDetails = dispute.resolutionDetails;
            this.dateOpened = dispute.dateOpened;
            return this;
        }

        public Dispute build() { return new Dispute(this); }
    }
}