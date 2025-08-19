package za.ac.cput.factory;

import za.ac.cput.domain.*;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;

public class VerificationFactory {

    public static Verification createVerification(User user, Verification.DocumentType documentType,
                                                  String documentUrl, String verificationCode) {

        // Validate inputs
        if (user == null || documentType == null) {
            return null;
        }
        if (ValidationHelper.isNullOrEmpty(documentUrl)) {
            return null;
        }

        return new Verification.Builder()
                .setVerificationId(ValidationHelper.generateId())
                .setUser(user)
                .setDocumentType(documentType)
                .setDocumentUrl(documentUrl.trim())
                .setVerificationCode(verificationCode != null ? verificationCode.trim() : null)
                .setStatus(Verification.VerificationStatus.PENDING)
                .setDateSubmitted(LocalDateTime.now())
                .setDateVerified(null)
                .build();
    }

    public static Verification createIdVerification(User user, String documentUrl) {
        return createVerification(user, Verification.DocumentType.ID, documentUrl, null);
    }

    public static Verification createCertificateVerification(User user, String documentUrl) {
        return createVerification(user, Verification.DocumentType.CERTIFICATE, documentUrl, null);
    }

    public static Verification createLicenseVerification(User user, String documentUrl) {
        return createVerification(user, Verification.DocumentType.LICENSE, documentUrl, null);
    }
}