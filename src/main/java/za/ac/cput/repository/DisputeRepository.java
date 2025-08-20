package za.ac.cput.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;
import za.ac.cput.domain.*;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, String> {

    // Find disputes by contract
    List<Dispute> findByContract(Contract contract);

    // Find disputes by opened by user
    List<Dispute> findByOpenedBy(User openedBy);

    // Find disputes by status
    List<Dispute> findByStatus(Dispute.DisputeStatus status);

    // Find open disputes
    @Query("SELECT d FROM Dispute d WHERE d.status = 'OPEN'")
    List<Dispute> findOpenDisputes();

    // Find disputes opened within date range
    @Query("SELECT d FROM Dispute d WHERE d.dateOpened BETWEEN :startDate AND :endDate")
    List<Dispute> findByDateOpenedBetween(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    // Count disputes by status
    Long countByStatus(Dispute.DisputeStatus status);

    // Find disputes involving a specific user (either as client or worker in contract)
    @Query("SELECT d FROM Dispute d WHERE d.contract.client = :user OR d.contract.worker = :user")
    List<Dispute> findByContractParticipant(@Param("user") User user);
}