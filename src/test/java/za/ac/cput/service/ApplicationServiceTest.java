package za.ac.cput.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import za.ac.cput.domain.*;
import za.ac.cput.factory.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.class)
class ApplicationServiceTest {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    private static Application testApplication;
    private static Application secondApplication;
    private static Job testJob;
    private static User clientUser;
    private static User workerUser1;
    private static User workerUser2;

    @BeforeAll
    static void setup() {
        clientUser = UserFactory.createClientUser(
                "App", "Client", "appclient@test.com", "Password123!"
        );

        workerUser1 = UserFactory.createWorkerUser(
                "Worker", "One", "worker1@test.com", "Password123!", "0821111111"
        );

        workerUser2 = UserFactory.createWorkerUser(
                "Worker", "Two", "worker2@test.com", "Password123!", "0822222222"
        );
    }

    @Test
    @Order(1)
    void createUsers() {
        clientUser = userService.create(clientUser);
        workerUser1 = userService.create(workerUser1);
        workerUser2 = userService.create(workerUser2);

        assertNotNull(clientUser.getUserId());
        assertNotNull(workerUser1.getUserId());
        assertNotNull(workerUser2.getUserId());
        System.out.println("Created test users for application service");
    }

    @Test
    @Order(2)
    void createJob() {
        testJob = jobService.postJob(
                clientUser,
                "Frontend Development",
                "Need a React developer for a dashboard project",
                "Technology",
                4000.0,
                "Cape Town"
        );

        assertNotNull(testJob);
        System.out.println("Created test job: " + testJob);
    }

    @Test
    @Order(3)
    void applyForJob() {
        testApplication = applicationService.applyForJob(
                testJob,
                workerUser1,
                "I have 5 years of React experience and would love to work on this project.",
                3800.0
        );

        assertNotNull(testApplication);
        assertNotNull(testApplication.getApplicationId());
        assertEquals(Application.ApplicationStatus.PENDING, testApplication.getStatus());
        assertEquals(3800.0, testApplication.getExpectedPay());
        System.out.println("Created application: " + testApplication);
    }

    @Test
    @Order(4)
    void applyForJob_SecondApplication() {
        secondApplication = applicationService.applyForJob(
                testJob,
                workerUser2,
                "I'm a senior React developer with extensive dashboard experience.",
                3500.0
        );

        assertNotNull(secondApplication);
        assertEquals(Application.ApplicationStatus.PENDING, secondApplication.getStatus());
        System.out.println("Created second application: " + secondApplication);
    }

    @Test
    @Order(5)
    void applyForJob_DuplicateApplication_ShouldFail() {
        assertThrows(IllegalStateException.class, () -> {
            applicationService.applyForJob(
                    testJob,
                    workerUser1,
                    "Another application from same worker",
                    3000.0
            );
        });
        System.out.println("Correctly prevented duplicate application");
    }

    @Test
    @Order(6)
    void create_InvalidApplication_ShouldFail() {
        Application invalidApp = new Application.Builder()
                .setApplicationId("invalid-id")
                .setJob(testJob)
                .setWorker(workerUser1)
                .setCoverLetter("") // Too short
                .setExpectedPay(-1000.0) // Invalid negative amount
                .setStatus(Application.ApplicationStatus.PENDING)
                .setDateApplied(LocalDateTime.now())
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.create(invalidApp);
        });
        System.out.println("Correctly prevented invalid application creation");
    }

    @Test
    @Order(7)
    void read() {
        Application readApplication = applicationService.read(testApplication.getApplicationId());
        assertNotNull(readApplication);
        assertEquals(testApplication.getApplicationId(), readApplication.getApplicationId());
        System.out.println("Read application: " + readApplication);
    }

    @Test
    @Order(8)
    void rejectApplication() {
        secondApplication = applicationService.rejectApplication(
                secondApplication.getApplicationId(),
                "We chose another candidate"
        );
        assertEquals(Application.ApplicationStatus.REJECTED, secondApplication.getStatus());
        System.out.println("Rejected second application");
    }

    @Test
    @Order(9)
    void acceptApplication() {
        testApplication = applicationService.acceptApplication(testApplication.getApplicationId());
        assertEquals(Application.ApplicationStatus.ACCEPTED, testApplication.getStatus());
        System.out.println("Accepted first application");
    }

    @Test
    @Order(10)
    void acceptApplication_NonPendingApplication_ShouldFail() {
        assertThrows(IllegalStateException.class, () -> {
            applicationService.acceptApplication(testApplication.getApplicationId());
        });
        System.out.println("Correctly prevented accepting non-pending application");
    }

    @Test
    @Order(11)
    void withdrawApplication() {
        // Create new application to withdraw
        User workerUser3 = UserFactory.createWorkerUser(
                "Worker", "Three", "worker3@test.com", "Password123!", "0823333333"
        );
        workerUser3 = userService.create(workerUser3);

        Job newJob = jobService.postJob(
                clientUser,
                "Backend Development",
                "Need a Node.js developer",
                "Technology",
                3000.0,
                "Cape Town"
        );

        Application withdrawApp = applicationService.applyForJob(
                newJob,
                workerUser3,
                "I'm interested in this backend project",
                2800.0
        );

        withdrawApp = applicationService.withdrawApplication(withdrawApp.getApplicationId());
        assertEquals(Application.ApplicationStatus.WITHDRAWN, withdrawApp.getStatus());
        System.out.println("Withdrew application");
    }

    @Test
    @Order(12)
    void findApplicationsByWorker() {
        List<Application> workerApplications = applicationService.findApplicationsByWorker(workerUser1);
        assertFalse(workerApplications.isEmpty());
        assertTrue(workerApplications.stream()
                .allMatch(app -> app.getWorker().getUserId().equals(workerUser1.getUserId())));
        System.out.println("Found applications for worker: " + workerApplications.size());
    }

    @Test
    @Order(13)
    void findApplicationsByJob() {
        List<Application> jobApplications = applicationService.findApplicationsByJob(testJob);
        assertFalse(jobApplications.isEmpty());
        assertTrue(jobApplications.stream()
                .allMatch(app -> app.getJob().getJobId().equals(testJob.getJobId())));
        System.out.println("Found applications for job: " + jobApplications.size());
    }

    @Test
    @Order(14)
    void findApplicationsByJobClient() {
        List<Application> clientApplications = applicationService.findApplicationsByJobClient(clientUser);
        assertFalse(clientApplications.isEmpty());
        System.out.println("Found applications for client's jobs: " + clientApplications.size());
    }

    @Test
    @Order(15)
    void findPendingApplications() {
        List<Application> pendingApps = applicationService.findPendingApplications();
        assertTrue(pendingApps.stream()
                .allMatch(app -> app.getStatus() == Application.ApplicationStatus.PENDING));
        System.out.println("Found pending applications: " + pendingApps.size());
    }

    @Test
    @Order(16)
    void findRecentApplications() {
        List<Application> recentApps = applicationService.findRecentApplications(7);
        assertFalse(recentApps.isEmpty());
        System.out.println("Found recent applications (7 days): " + recentApps.size());
    }

    @Test
    @Order(17)
    void countApplicationsByStatus() {
        Long pendingCount = applicationService.countApplicationsByStatus(Application.ApplicationStatus.PENDING);
        Long acceptedCount = applicationService.countApplicationsByStatus(Application.ApplicationStatus.ACCEPTED);
        Long rejectedCount = applicationService.countApplicationsByStatus(Application.ApplicationStatus.REJECTED);

        assertTrue(pendingCount >= 0);
        assertTrue(acceptedCount >= 0);
        assertTrue(rejectedCount >= 0);

        System.out.println("Pending: " + pendingCount + ", Accepted: " + acceptedCount + ", Rejected: " + rejectedCount);
    }

    @Test
    @Order(18)
    void delete() {
        Application deletedApp = applicationService.delete(testApplication.getApplicationId());
        assertNotNull(deletedApp);
        assertEquals(testApplication.getApplicationId(), deletedApp.getApplicationId());

        assertThrows(RuntimeException.class, () -> {
            applicationService.read(testApplication.getApplicationId());
        });
        System.out.println("Deleted application: " + deletedApp.getApplicationId());
    }
}
