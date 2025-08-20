package za.ac.cput.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;
import za.ac.cput.domain.*;

import java.util.*;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, String> {

    // Find verifications by user
    List<Verification> findByUser(User user);

    // Find verifications by document type
    List<Verification> findByDocumentType(Verification.DocumentType documentType);

    // Find verifications by status
    List<Verification> findByStatus(Verification.VerificationStatus status);

    // Find pending verifications
    @Query("SELECT v FROM Verification v WHERE v.status = 'PENDING'")
    List<Verification> findPendingVerifications();

    // Find verifications by user and status
    List<Verification> findByUserAndStatus(User user, Verification.VerificationStatus status);

    // Check if user has verified document type
    @Query("SELECT COUNT(v) > 0 FROM Verification v WHERE v.user = :user AND v.documentType = :documentType AND v.status = 'VERIFIED'")
    boolean hasVerifiedDocument(@Param("user") User user, @Param("documentType") Verification.DocumentType documentType);

    // Find verifications by verification code
    Optional<Verification> findByVerificationCode(String verificationCode);
}
