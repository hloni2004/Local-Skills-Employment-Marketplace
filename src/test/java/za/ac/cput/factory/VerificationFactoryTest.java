package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.User;
import za.ac.cput.domain.Verification;
import static org.junit.jupiter.api.Assertions.*;

class VerificationFactoryTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFactory.createWorkerUser("Test", "User", "test@email.com", "Password123!", "0821234567");
    }

    @Test
    void createVerification() {
        String documentUrl = "https://example.com/documents/id-document.pdf";
        String verificationCode = "VERIFY123";

        Verification verification = VerificationFactory.createVerification(user,
                Verification.DocumentType.ID, documentUrl, verificationCode);

        assertNotNull(verification);
        assertEquals(user, verification.getUser());
        assertEquals(Verification.DocumentType.ID, verification.getDocumentType());
        assertEquals(documentUrl, verification.getDocumentUrl());
        assertEquals(verificationCode, verification.getVerificationCode());
        assertEquals(Verification.VerificationStatus.PENDING, verification.getStatus());
        assertNotNull(verification.getVerificationId());
        assertNotNull(verification.getDateSubmitted());
        assertNull(verification.getDateVerified());
        System.out.println(verification);
    }

    @Test
    void createVerification_WithoutVerificationCode() {
        String documentUrl = "https://example.com/documents/certificate.pdf";

        Verification verification = VerificationFactory.createVerification(user,
                Verification.DocumentType.CERTIFICATE, documentUrl, null);

        assertNotNull(verification);
        assertNull(verification.getVerificationCode());
    }

    @Test
    void createVerification_InvalidUser() {
        Verification verification = VerificationFactory.createVerification(null,
                Verification.DocumentType.ID, "https://example.com/doc.pdf", null);
        assertNull(verification);
    }

    @Test
    void createVerification_InvalidDocumentType() {
        Verification verification = VerificationFactory.createVerification(user,
                null, "https://example.com/doc.pdf", null);
        assertNull(verification);
    }

    @Test
    void createVerification_InvalidDocumentUrl() {
        // Test with null document URL
        Verification verification = VerificationFactory.createVerification(user,
                Verification.DocumentType.ID, null, null);
        assertNull(verification);

        // Test with empty document URL
        verification = VerificationFactory.createVerification(user,
                Verification.DocumentType.ID, "", null);
        assertNull(verification);

        // Test with whitespace-only document URL
        verification = VerificationFactory.createVerification(user,
                Verification.DocumentType.ID, "   ", null);
        assertNull(verification);
    }

    @Test
    void createIdVerification() {
        String documentUrl = "https://example.com/documents/national-id.pdf";
        Verification verification = VerificationFactory.createIdVerification(user, documentUrl);

        assertNotNull(verification);
        assertEquals(Verification.DocumentType.ID, verification.getDocumentType());
        assertEquals(documentUrl, verification.getDocumentUrl());
        assertNull(verification.getVerificationCode());
        System.out.println(verification);
    }

    @Test
    void createCertificateVerification() {
        String documentUrl = "https://example.com/documents/diploma.pdf";
        Verification verification = VerificationFactory.createCertificateVerification(user, documentUrl);

        assertNotNull(verification);
        assertEquals(Verification.DocumentType.CERTIFICATE, verification.getDocumentType());
        assertEquals(documentUrl, verification.getDocumentUrl());
        System.out.println(verification);
    }

    @Test
    void createLicenseVerification() {
        String documentUrl = "https://example.com/documents/professional-license.pdf";
        Verification verification = VerificationFactory.createLicenseVerification(user, documentUrl);

        assertNotNull(verification);
        assertEquals(Verification.DocumentType.LICENSE, verification.getDocumentType());
        assertEquals(documentUrl, verification.getDocumentUrl());
        System.out.println(verification);
    }

    @Test
    void createIdVerification_InvalidInput() {
        Verification verification = VerificationFactory.createIdVerification(user, null);
        assertNull(verification);
    }

    @Test
    void createCertificateVerification_InvalidInput() {
        Verification verification = VerificationFactory.createCertificateVerification(null, "https://example.com/doc.pdf");
        assertNull(verification);
    }

    @Test
    void createLicenseVerification_InvalidInput() {
        Verification verification = VerificationFactory.createLicenseVerification(user, "");
        assertNull(verification);
    }
}