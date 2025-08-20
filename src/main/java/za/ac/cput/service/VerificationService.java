package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.User;
import za.ac.cput.domain.Verification;
import za.ac.cput.repository.VerificationRepository;
import za.ac.cput.factory.VerificationFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VerificationService implements IService<Verification, String> {

    private final VerificationRepository verificationRepository;
    private final NotificationService notificationService;

    @Autowired
    public VerificationService(VerificationRepository verificationRepository,
                               NotificationService notificationService) {
        this.verificationRepository = verificationRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Verification create(Verification verification) {
        if (verification == null) {
            throw new IllegalArgumentException("Verification cannot be null");
        }

        Verification savedVerification = verificationRepository.save(verification);

        // Notify user that verification was submitted
        notificationService.createSystemNotification(
                verification.getUser(),
                "Your " + verification.getDocumentType().name().toLowerCase() +
                        " verification has been submitted and is pending review"
        );

        return savedVerification;
    }

    @Override
    public Verification read(String verificationId) {
        return verificationRepository.findById(verificationId)
                .orElseThrow(() -> new RuntimeException("Verification not found with ID: " + verificationId));
    }

    @Override
    public Verification update(Verification verification) {
        if (!verificationRepository.existsById(verification.getVerificationId())) {
            throw new RuntimeException("Verification not found");
        }
        return verificationRepository.save(verification);
    }

    @Override
    public Verification delete(String verificationId) {
        Verification verification = read(verificationId);
        verificationRepository.deleteById(verificationId);
        return verification;
    }

    // Business Logic Methods
    public Verification submitVerification(User user, Verification.DocumentType documentType,
                                           String documentUrl, String verificationCode) {
        Verification verification = VerificationFactory.createVerification(
                user, documentType, documentUrl, verificationCode);
        if (verification == null) {
            throw new IllegalArgumentException("Invalid verification creation data");
        }
        return create(verification);
    }

    public Verification submitIdVerification(User user, String documentUrl) {
        return submitVerification(user, Verification.DocumentType.ID, documentUrl, null);
    }

    public Verification submitCertificateVerification(User user, String documentUrl) {
        return submitVerification(user, Verification.DocumentType.CERTIFICATE, documentUrl, null);
    }

    public Verification submitLicenseVerification(User user, String documentUrl) {
        return submitVerification(user, Verification.DocumentType.LICENSE, documentUrl, null);
    }

    public Verification approveVerification(String verificationId, String approvedBy) {
        Verification verification = read(verificationId);

        if (verification.getStatus() != Verification.VerificationStatus.PENDING) {
            throw new IllegalStateException("Verification is not pending");
        }

        Verification approvedVerification = update(new Verification.Builder()
                .copy(verification)
                .setStatus(Verification.VerificationStatus.VERIFIED)
                .setDateVerified(LocalDateTime.now())
                .build());

        // Notify user of approval
        notificationService.createSystemNotification(
                verification.getUser(),
                "Your " + verification.getDocumentType().name().toLowerCase() +
                        " verification has been approved!"
        );

        return approvedVerification;
    }

    public Verification rejectVerification(String verificationId, String reason) {
        Verification verification = read(verificationId);

        if (verification.getStatus() != Verification.VerificationStatus.PENDING) {
            throw new IllegalStateException("Verification is not pending");
        }

        Verification rejectedVerification = update(new Verification.Builder()
                .copy(verification)
                .setStatus(Verification.VerificationStatus.REJECTED)
                .setDateVerified(LocalDateTime.now())
                .build());

        // Notify user of rejection
        notificationService.createSystemNotification(
                verification.getUser(),
                "Your " + verification.getDocumentType().name().toLowerCase() +
                        " verification has been rejected. " + (reason != null ? "Reason: " + reason : "")
        );

        return rejectedVerification;
    }

    public List<Verification> findVerificationsByUser(User user) {
        return verificationRepository.findByUser(user);
    }

    public List<Verification> findVerificationsByDocumentType(Verification.DocumentType documentType) {
        return verificationRepository.findByDocumentType(documentType);
    }

    public List<Verification> findVerificationsByStatus(Verification.VerificationStatus status) {
        return verificationRepository.findByStatus(status);
    }

    public List<Verification> findPendingVerifications() {
        return verificationRepository.findPendingVerifications();
    }

    public List<Verification> findUserVerificationsByStatus(User user, Verification.VerificationStatus status) {
        return verificationRepository.findByUserAndStatus(user, status);
    }

    public Optional<Verification> findByVerificationCode(String verificationCode) {
        return verificationRepository.findByVerificationCode(verificationCode);
    }

    public boolean hasVerifiedDocument(User user, Verification.DocumentType documentType) {
        return verificationRepository.hasVerifiedDocument(user, documentType);
    }

    public boolean isUserVerified(User user) {
        // Check if user has at least ID verified
        return hasVerifiedDocument(user, Verification.DocumentType.ID);
    }

    public boolean isUserFullyVerified(User user) {
        // Check if user has all document types verified
        return hasVerifiedDocument(user, Verification.DocumentType.ID) &&
                hasVerifiedDocument(user, Verification.DocumentType.CERTIFICATE);
    }

    public List<Verification> getVerificationsRequiringReview() {
        return findPendingVerifications();
    }

    public long countPendingVerifications() {
        return findPendingVerifications().size();
    }

    public long countVerificationsByStatus(Verification.VerificationStatus status) {
        return findVerificationsByStatus(status).size();
    }

    public boolean canSubmitVerification(User user, Verification.DocumentType documentType) {
        // Check if user already has a pending or verified document of this type
        List<Verification> existingVerifications = verificationRepository.findByUserAndStatus(
                user, Verification.VerificationStatus.VERIFIED);
        List<Verification> pendingVerifications = verificationRepository.findByUserAndStatus(
                user, Verification.VerificationStatus.PENDING);

        boolean hasVerified = existingVerifications.stream()
                .anyMatch(v -> v.getDocumentType() == documentType);
        boolean hasPending = pendingVerifications.stream()
                .anyMatch(v -> v.getDocumentType() == documentType);

        return !hasVerified && !hasPending;
    }

    public List<Verification.DocumentType> getRequiredDocuments() {
        return List.of(
                Verification.DocumentType.ID,
                Verification.DocumentType.CERTIFICATE
        );
    }

    public List<Verification.DocumentType> getMissingDocuments(User user) {
        return getRequiredDocuments().stream()
                .filter(docType -> !hasVerifiedDocument(user, docType))
                .toList();
    }
}