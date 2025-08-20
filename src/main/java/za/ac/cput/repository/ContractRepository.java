package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Contract;
import za.ac.cput.domain.Job;
import za.ac.cput.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {

    // Find contracts by client
    List<Contract> findByClient(User client);

    // Find contracts by worker
    List<Contract> findByWorker(User worker);

    // Find contracts by job
    Optional<Contract> findByJob(Job job);

    // Find contracts by status
    List<Contract> findByStatus(Contract.ContractStatus status);

    // Find active contracts
    @Query("SELECT c FROM Contract c WHERE c.status = 'ACTIVE'")
    List<Contract> findActiveContracts();

    // Find completed contracts
    @Query("SELECT c FROM Contract c WHERE c.status = 'COMPLETED'")
    List<Contract> findCompletedContracts();

    // Find contracts by client and status
    List<Contract> findByClientAndStatus(User client, Contract.ContractStatus status);

    // Find contracts by worker and status
    List<Contract> findByWorkerAndStatus(User worker, Contract.ContractStatus status);

    // Find contracts starting within date range
    @Query("SELECT c FROM Contract c WHERE c.startDate BETWEEN :startDate AND :endDate")
    List<Contract> findByStartDateBetween(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    // Find contracts ending within date range
    @Query("SELECT c FROM Contract c WHERE c.endDate BETWEEN :startDate AND :endDate")
    List<Contract> findByEndDateBetween(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    // Find contracts by agreed pay range
    @Query("SELECT c FROM Contract c WHERE c.agreedPay BETWEEN :minPay AND :maxPay")
    List<Contract> findByAgreedPayRange(@Param("minPay") Double minPay, @Param("maxPay") Double maxPay);

    // Find overdue contracts (end date passed but still active)
    @Query("SELECT c FROM Contract c WHERE c.endDate < :currentDate AND c.status = 'ACTIVE'")
    List<Contract> findOverdueContracts(@Param("currentDate") LocalDateTime currentDate);

    // Find contracts involving a specific user (either as client or worker)
    @Query("SELECT c FROM Contract c WHERE c.client = :user OR c.worker = :user")
    List<Contract> findByClientOrWorker(@Param("user") User user);

    // Count contracts by status
    Long countByStatus(Contract.ContractStatus status);

    // Find contracts with disputes
    @Query("SELECT c FROM Contract c WHERE c.status = 'DISPUTED'")
    List<Contract> findDisputedContracts();

    // Find the highest value contracts
    @Query("SELECT c FROM Contract c ORDER BY c.agreedPay DESC")
    List<Contract> findHighestValueContracts();

    // Check if user has active contracts
    @Query("SELECT COUNT(c) > 0 FROM Contract c WHERE (c.client = :user OR c.worker = :user) AND c.status = 'ACTIVE'")
    boolean hasActiveContracts(@Param("user") User user);

    // Find contracts by job category
    @Query("SELECT c FROM Contract c WHERE c.job.category = :category")
    List<Contract> findByJobCategory(@Param("category") String category);
}