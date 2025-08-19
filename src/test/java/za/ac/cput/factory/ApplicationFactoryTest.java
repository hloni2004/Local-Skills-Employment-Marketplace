package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Application;
import za.ac.cput.domain.Job;
import za.ac.cput.domain.User;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ApplicationFactoryTest {

    private Job job;
    private User worker;

    @BeforeEach
    void setUp() {
        User client = UserFactory.createClientUser("Client", "User", "client@email.com", "Password123!");
        job = JobFactory.createJob(client, "Web Developer Position",
                "Looking for an experienced web developer", "Programming", 20000.0, "Cape Town");
        worker = UserFactory.createWorkerUser("Worker", "User", "worker@email.com", "Password123!", "0821234567");
    }

    @Test
    void createApplication() {
        String coverLetter = "I am an experienced developer with 5 years of experience in web development.";
        Application application = ApplicationFactory.createApplication(job, worker, coverLetter, 18000.0);

        assertNotNull(application);
        assertEquals(job, application.getJob());
        assertEquals(worker, application.getWorker());
        assertEquals(coverLetter, application.getCoverLetter());
        assertEquals(18000.0, application.getExpectedPay());
        assertEquals(Application.ApplicationStatus.PENDING, application.getStatus());
        assertNotNull(application.getApplicationId());
        assertNotNull(application.getDateApplied());
    }

    @Test
    void createApplication_WithoutCoverLetter() {
        Application application = ApplicationFactory.createApplication(job, worker, null, 18000.0);

        assertNotNull(application);
        assertNull(application.getCoverLetter());
        assertEquals(18000.0, application.getExpectedPay());
    }

    @Test
    void createApplication_InvalidJob() {
        Application application = ApplicationFactory.createApplication(null, worker, "Cover letter", 18000.0);
        assertNull(application);
    }

    @Test
    void createApplication_InvalidWorker() {
        Application application = ApplicationFactory.createApplication(job, null, "Cover letter", 18000.0);
        assertNull(application);
    }

    @Test
    void createApplication_InvalidCoverLetter() {
        // Test with cover letter too short
        Application application = ApplicationFactory.createApplication(job, worker, "Short", 18000.0);
        assertNull(application);

        // Test with cover letter too long
        application = ApplicationFactory.createApplication(job, worker, "A".repeat(1001), 18000.0);
        assertNull(application);
    }

    @Test
    void createApplication_InvalidExpectedPay() {
        // Test with negative expected pay
        Application application = ApplicationFactory.createApplication(job, worker,
                "Valid cover letter for this application", -1000.0);
        assertNull(application);

        // Test with zero expected pay
        application = ApplicationFactory.createApplication(job, worker,
                "Valid cover letter for this application", 0.0);
        assertNull(application);
    }

    @Test
    void createQuickApplication() {
        Application application = ApplicationFactory.createQuickApplication(job, worker, 15000.0);

        assertNotNull(application);
        assertEquals(job, application.getJob());
        assertEquals(worker, application.getWorker());
        assertNull(application.getCoverLetter());
        assertEquals(15000.0, application.getExpectedPay());
        assertEquals(Application.ApplicationStatus.PENDING, application.getStatus());
    }

    @Test
    void createQuickApplication_InvalidInput() {
        Application application = ApplicationFactory.createQuickApplication(null, worker, 15000.0);
        assertNull(application);
    }
}
