package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "contract")
public class Contract {
    @Id
    @Column(name = "contract_id")
    protected String contractId;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    protected Job job;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    protected User client;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    protected User worker;

    @Column(name = "start_date")
    protected LocalDateTime startDate;

    @Column(name = "end_date")
    protected LocalDateTime endDate;

    @Column(name = "agreed_pay")
    protected Double agreedPay;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected ContractStatus status;

    @Column(name = "terms", columnDefinition = "TEXT")
    protected String terms;

    @OneToMany(mappedBy = "contract")
    protected List<Payment> payments;

    @OneToMany(mappedBy = "contract")
    protected List<Review> reviews;

    protected Contract() {}

    public Contract(Builder builder) {
        this.contractId = builder.contractId;
        this.job = builder.job;
        this.client = builder.client;
        this.worker = builder.worker;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.agreedPay = builder.agreedPay;
        this.status = builder.status;
        this.terms = builder.terms;
        this.payments = builder.payments;
        this.reviews = builder.reviews;
    }

    // Getters
    public String getContractId() { return contractId; }
    public Job getJob() { return job; }
    public User getClient() { return client; }
    public User getWorker() { return worker; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public Double getAgreedPay() { return agreedPay; }
    public ContractStatus getStatus() { return status; }
    public String getTerms() { return terms; }
    public List<Payment> getPayments() { return payments; }
    public List<Review> getReviews() { return reviews; }

    // Enum for Contract Status
    public enum ContractStatus {
        ACTIVE, COMPLETED, CANCELLED, DISPUTED, TERMINATED
    }

    @Override
    public String toString() {
        return "Contract{" +
                "contractId='" + contractId + '\'' +
                ", job=" + job.getJobId() +
                ", client=" + client.getUserId() +
                ", worker=" + worker.getUserId() +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", agreedPay=" + agreedPay +
                ", status=" + status +
                ", terms='" + terms + '\'' +
                '}';
    }

    public static class Builder {
        private String contractId;
        private Job job;
        private User client;
        private User worker;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Double agreedPay;
        private ContractStatus status;
        private String terms;
        private List<Payment> payments;
        private List<Review> reviews;

        public Builder setContractId(String contractId) { this.contractId = contractId; return this; }
        public Builder setJob(Job job) { this.job = job; return this; }
        public Builder setClient(User client) { this.client = client; return this; }
        public Builder setWorker(User worker) { this.worker = worker; return this; }
        public Builder setStartDate(LocalDateTime startDate) { this.startDate = startDate; return this; }
        public Builder setEndDate(LocalDateTime endDate) { this.endDate = endDate; return this; }
        public Builder setAgreedPay(Double agreedPay) { this.agreedPay = agreedPay; return this; }
        public Builder setStatus(ContractStatus status) { this.status = status; return this; }
        public Builder setTerms(String terms) { this.terms = terms; return this; }
        public Builder setPayments(List<Payment> payments) { this.payments = payments; return this; }
        public Builder setReviews(List<Review> reviews) { this.reviews = reviews; return this; }

        public Builder copy(Contract contract) {
            this.contractId = contract.contractId;
            this.job = contract.job;
            this.client = contract.client;
            this.worker = contract.worker;
            this.startDate = contract.startDate;
            this.endDate = contract.endDate;
            this.agreedPay = contract.agreedPay;
            this.status = contract.status;
            this.terms = contract.terms;
            this.payments = contract.payments;
            this.reviews = contract.reviews;
            return this;
        }

        public Contract build() { return new Contract(this); }
    }
}