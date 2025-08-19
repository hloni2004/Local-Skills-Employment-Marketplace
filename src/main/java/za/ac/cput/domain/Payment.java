package za.ac.cput.domain;

import jakarta.persistence.*;
import za.ac.cput.domain.Contract;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @Column(name = "payment_id")
    protected String paymentId;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    protected Contract contract;

    @Column(name = "amount")
    protected Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method")
    protected PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected PaymentStatus status;

    @Column(name = "date_processed")
    protected LocalDateTime dateProcessed;

    protected Payment() {}

    public Payment(Builder builder) {
        this.paymentId = builder.paymentId;
        this.contract = builder.contract;
        this.amount = builder.amount;
        this.method = builder.method;
        this.status = builder.status;
        this.dateProcessed = builder.dateProcessed;
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public Contract getContract() { return contract; }
    public Double getAmount() { return amount; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getDateProcessed() { return dateProcessed; }

    // Enums
    public enum PaymentMethod { CARD, MOBILE_MONEY, EFT, CASH }
    public enum PaymentStatus { PENDING, ESCROW, RELEASED, REFUNDED }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", contract=" + contract.getContractId() +
                ", amount=" + amount +
                ", method=" + method +
                ", status=" + status +
                ", dateProcessed=" + dateProcessed +
                '}';
    }

    public static class Builder {
        private String paymentId;
        private Contract contract;
        private Double amount;
        private PaymentMethod method;
        private PaymentStatus status;
        private LocalDateTime dateProcessed;

        public Builder setPaymentId(String paymentId) { this.paymentId = paymentId; return this; }
        public Builder setContract(Contract contract) { this.contract = contract; return this; }
        public Builder setAmount(Double amount) { this.amount = amount; return this; }
        public Builder setMethod(PaymentMethod method) { this.method = method; return this; }
        public Builder setStatus(PaymentStatus status) { this.status = status; return this; }
        public Builder setDateProcessed(LocalDateTime dateProcessed) { this.dateProcessed = dateProcessed; return this; }

        public Builder copy(Payment payment) {
            this.paymentId = payment.paymentId;
            this.contract = payment.contract;
            this.amount = payment.amount;
            this.method = payment.method;
            this.status = payment.status;
            this.dateProcessed = payment.dateProcessed;
            return this;
        }

        public Payment build() { return new Payment(this); }
    }
}