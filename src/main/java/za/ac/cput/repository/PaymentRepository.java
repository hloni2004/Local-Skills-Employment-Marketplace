package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Contract;
import za.ac.cput.domain.Payment;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    // Find payments by contract
    List<Payment> findByContract(Contract contract);

    // Find payments by status
    List<Payment> findByStatus(Payment.PaymentStatus status);

    // Find payments by method
    List<Payment> findByMethod(Payment.PaymentMethod method);

    // Find pending payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING'")
    List<Payment> findPendingPayments();

    // Find escrow payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'ESCROW'")
    List<Payment> findEscrowPayments();

    // Find released payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'RELEASED'")
    List<Payment> findReleasedPayments();

    // Find payments by contract and status
    List<Payment> findByContractAndStatus(Contract contract, Payment.PaymentStatus status);

    // Find payments processed within date range
    @Query("SELECT p FROM Payment p WHERE p.dateProcessed BETWEEN :startDate AND :endDate")
    List<Payment> findByDateProcessedBetween(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    // Find payments by amount range
    @Query("SELECT p FROM Payment p WHERE p.amount BETWEEN :minAmount AND :maxAmount")
    List<Payment> findByAmountRange(@Param("minAmount") Double minAmount, @Param("maxAmount") Double maxAmount);

    // Find payments for a specific client's contracts
    @Query("SELECT p FROM Payment p WHERE p.contract.client = :client")
    List<Payment> findByContractClient(@Param("client") za.ac.cput.domain.User client);

    // Find payments for a specific worker's contracts
    @Query("SELECT p FROM Payment p WHERE p.contract.worker = :worker")
    List<Payment> findByContractWorker(@Param("worker") za.ac.cput.domain.User worker);

    // Calculate total amount by status
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    Double getTotalAmountByStatus(@Param("status") Payment.PaymentStatus status);

    // Calculate total escrow amount
    @Query("SELECT COALESCE(SUM(p.amount), 0.0) FROM Payment p WHERE p.status = 'ESCROW'")
    Double getTotalEscrowAmount();

    // Find largest payments
    @Query("SELECT p FROM Payment p ORDER BY p.amount DESC")
    List<Payment> findLargestPayments();

    // Count payments by method
    Long countByMethod(Payment.PaymentMethod method);

    // Count payments by status
    Long countByStatus(Payment.PaymentStatus status);

    // Find recent payments (processed within last N days)
    @Query("SELECT p FROM Payment p WHERE p.dateProcessed >= :date ORDER BY p.dateProcessed DESC")
    List<Payment> findRecentPayments(@Param("date") LocalDateTime date);

    // Find payments requiring attention (pending for too long)
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.dateProcessed < :date")
    List<Payment> findStuckPayments(@Param("date") LocalDateTime date);
}