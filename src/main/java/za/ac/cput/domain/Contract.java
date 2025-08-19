package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    // Enums
    public enum ContractStatus { ACTIVE, COMPLETED, DISPUTED, CANCELLED }

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
                '}';
    }

    public static class Builder {
        private String contractId;
        private Job job;
        private User client;
        private User worker;
        private LocalDateTime startDate;
        LocalDateTime endDate;
        private Double agreedPay;
        private ContractStatus status;

        public Builder setContractId(String contractId) { this.contractId = contractId; return this; }
        public Builder setJob(Job job) { this.job = job; return this; }
        public Builder setClient(User client) { this.client = client; return this; }
        public Builder setWorker(User worker) { this.worker = worker; return this; }
        public Builder setStartDate(LocalDateTime startDate) { this.startDate = startDate; return this; }
        public Builder setEndDate(LocalDateTime endDate) { this.endDate = endDate; return this; }
        public Builder setAgreedPay(Double agreedPay) { this.agreedPay = agreedPay; return this; }
        public Builder setStatus(ContractStatus status) { this.status = status; return this; }

        public Builder copy(Contract contract) {
            this.contractId = contract.contractId;
            this.job = contract.job;
            this.client = contract.client;
            this.worker = contract.worker;
            this.startDate = contract.startDate;
            this.endDate = contract.endDate;
            this.agreedPay = contract.agreedPay;
            this.status = contract.status;
            return this;
        }

        public Contract build() { return new Contract(this); }
    }
}