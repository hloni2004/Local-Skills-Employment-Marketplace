package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Contract;
import za.ac.cput.domain.Payment;
import za.ac.cput.domain.User;
import za.ac.cput.repository.PaymentRepository;
import za.ac.cput.factory.PaymentFactory;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentService implements IService<Payment, String> {

    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,
                          NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Payment create(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }

        if (!ValidationHelper.isValidBudget(payment.getAmount())) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        Payment savedPayment = paymentRepository.save(payment);

        // Notify relevant parties
        notifyPaymentCreated(savedPayment);

        return savedPayment;
    }

    @Override
    public Payment read(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
    }

    @Override
    public Payment update(Payment payment) {
        if (!paymentRepository.existsById(payment.getPaymentId())) {
            throw new RuntimeException("Payment not found");
        }
        return paymentRepository.save(payment);
    }

    @Override
    public Payment delete(String paymentId) {
        Payment payment = read(paymentId);
        if (payment.getStatus() == Payment.PaymentStatus.RELEASED) {
            throw new IllegalStateException("Cannot delete released payment");
        }
        paymentRepository.deleteById(paymentId);
        return payment;
    }

    // Business Logic Methods
    public Payment createPayment(Contract contract, Double amount, Payment.PaymentMethod method) {
        Payment payment = PaymentFactory.createPayment(contract, amount, method);
        if (payment == null) {
            throw new IllegalArgumentException("Invalid payment creation data");
        }
        return create(payment);
    }

    public Payment createEscrowPayment(Contract contract, Double amount) {
        Payment payment = PaymentFactory.createEscrowPayment(contract, amount);
        if (payment == null) {
            throw new IllegalArgumentException("Invalid escrow payment creation data");
        }
        return create(payment);
    }

    public Payment processPayment(String paymentId) {
        Payment payment = read(paymentId);

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not in pending status");
        }

        Payment processedPayment = update(new Payment.Builder()
                .copy(payment)
                .setStatus(Payment.PaymentStatus.ESCROW)
                .setDateProcessed(LocalDateTime.now())
                .build());

        // Notify client about payment processed
        notificationService.createPaymentNotification(
                payment.getContract().getClient(),
                "Payment of R" + payment.getAmount() + " has been processed and held in escrow"
        );

        return processedPayment;
    }

    public Payment releasePayment(String paymentId) {
        Payment payment = read(paymentId);

        if (payment.getStatus() != Payment.PaymentStatus.ESCROW) {
            throw new IllegalStateException("Payment is not in escrow");
        }

        Payment releasedPayment = update(new Payment.Builder()
                .copy(payment)
                .setStatus(Payment.PaymentStatus.RELEASED)
                .setDateProcessed(LocalDateTime.now())
                .build());

        // Notify worker about payment release
        notificationService.createPaymentNotification(
                payment.getContract().getWorker(),
                "Payment of R" + payment.getAmount() + " has been released to you"
        );

        // Notify client about payment release
        notificationService.createPaymentNotification(
                payment.getContract().getClient(),
                "Payment of R" + payment.getAmount() + " has been released to the worker"
        );

        return releasedPayment;
    }

    public Payment refundPayment(String paymentId, String reason) {
        Payment payment = read(paymentId);

        if (payment.getStatus() == Payment.PaymentStatus.RELEASED) {
            throw new IllegalStateException("Cannot refund released payment");
        }

        Payment refundedPayment = update(new Payment.Builder()
                .copy(payment)
                .setStatus(Payment.PaymentStatus.REFUNDED)
                .setDateProcessed(LocalDateTime.now())
                .build());

        // Notify client about refund
        notificationService.createPaymentNotification(
                payment.getContract().getClient(),
                "Payment of R" + payment.getAmount() + " has been refunded. Reason: " + reason
        );

        return refundedPayment;
    }

    public List<Payment> findPaymentsByContract(Contract contract) {
        return paymentRepository.findByContract(contract);
    }

    public List<Payment> findPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public List<Payment> findPendingPayments() {
        return paymentRepository.findPendingPayments();
    }

    public List<Payment> findEscrowPayments() {
        return paymentRepository.findEscrowPayments();
    }

    public List<Payment> findPaymentsByClient(User client) {
        return paymentRepository.findByContractClient(client);
    }

    public List<Payment> findPaymentsByWorker(User worker) {
        return paymentRepository.findByContractWorker(worker);
    }

    public List<Payment> findPaymentsByMethod(Payment.PaymentMethod method) {
        return paymentRepository.findByMethod(method);
    }

    public List<Payment> findPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByDateProcessedBetween(startDate, endDate);
    }

    public List<Payment> findRecentPayments(int days) {
        LocalDateTime date = LocalDateTime.now().minusDays(days);
        return paymentRepository.findRecentPayments(date);
    }

    public List<Payment> findStuckPayments(int days) {
        LocalDateTime date = LocalDateTime.now().minusDays(days);
        return paymentRepository.findStuckPayments(date);
    }

    public Double getTotalEscrowAmount() {
        return paymentRepository.getTotalEscrowAmount();
    }

    public Double getTotalAmountByStatus(Payment.PaymentStatus status) {
        return paymentRepository.getTotalAmountByStatus(status);
    }

    public Long countPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.countByStatus(status);
    }

    public Long countPaymentsByMethod(Payment.PaymentMethod method) {
        return paymentRepository.countByMethod(method);
    }

    private void notifyPaymentCreated(Payment payment) {
        // Notify client
        notificationService.createPaymentNotification(
                payment.getContract().getClient(),
                "Payment of R" + payment.getAmount() + " has been created for contract: " +
                        payment.getContract().getJob().getTitle()
        );

        // Notify worker if payment is in escrow
        if (payment.getStatus() == Payment.PaymentStatus.ESCROW) {
            notificationService.createPaymentNotification(
                    payment.getContract().getWorker(),
                    "Payment of R" + payment.getAmount() + " is being held in escrow for your work"
            );
        }
    }
}