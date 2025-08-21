package za.ac.cput.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import za.ac.cput.domain.*;
import za.ac.cput.factory.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JobServiceTest {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    private static Job testJob;
    private static Job secondJob;
    private static User clientUser;

    @BeforeAll
    static void setup() {
        clientUser = UserFactory.createClientUser(
                "Job", "Client", "jobclient@test.com", "Password123!"
        );
    }

    @Test
    @Order(1)
    void createUser() {
        clientUser = userService.create(clientUser);
        assertNotNull(clientUser.getUserId());
        System.out.println("Created test client user: " + clientUser);
    }

    @Test
    @Order(2)
    void postJob() {
        testJob = jobService.postJob(
                clientUser,
                "Web Development Project",
                "Need a skilled developer to build a modern e-commerce website using React and Node.js",
                "Technology",
                5000.0,
                "Cape Town"
        );

        assertNotNull(testJob);
        assertNotNull(testJob.getJobId());
        assertEquals("Web Development Project", testJob.getTitle());
        assertEquals(5000.0, testJob.getBudget());
        assertEquals(Job.JobStatus.OPEN, testJob.getStatus());
        System.out.println("Posted job: " + testJob);
    }

    @Test
    @Order(3)
    void create_InvalidJob_ShouldFail() {
        assertThrows(IllegalArgumentException.class, () -> {
            jobService.postJob(
                    clientUser,
                    "", // Invalid empty title
                    "Description",
                    "Technology",
                    5000.0,
                    "Cape Town"
            );
        });
        System.out.println("Correctly prevented invalid job creation");
    }

    @Test
    @Order(4)
    void postSecondJob() {
        secondJob = jobService.postJob(
                clientUser,
                "Mobile App Development",
                "Looking for an experienced mobile developer to create a fitness tracking app for Android and iOS",
                "Technology",
                7500.0,
                "Johannesburg"
        );

        assertNotNull(secondJob);
        assertEquals("Mobile App Development", secondJob.getTitle());
        System.out.println("Posted second job: " + secondJob);
    }

    @Test
    @Order(5)
    void read() {
        Job readJob = jobService.read(testJob.getJobId());
        assertNotNull(readJob);
        assertEquals(testJob.getJobId(), readJob.getJobId());
        assertEquals(testJob.getTitle(), readJob.getTitle());
        System.out.println("Read job: " + readJob);
    }

    @Test
    @Order(6)
    void update() {
        Job updatedJob = new Job.Builder()
                .copy(testJob)
                .setBudget(5500.0)
                .build();

        testJob = jobService.update(updatedJob);
        assertEquals(5500.0, testJob.getBudget());
        System.out.println("Updated job budget: " + testJob.getBudget());
    }

    @Test
    @Order(7)
    void assignJob() {
        User workerUser = UserFactory.createWorkerUser(
                "Test", "Worker", "testworker@test.com", "Password123!", "0821234567"
        );
        workerUser = userService.create(workerUser);

        testJob = jobService.assignJob(testJob.getJobId(), workerUser);
        assertEquals(Job.JobStatus.ASSIGNED, testJob.getStatus());
        System.out.println("Assigned job to worker");
    }

    @Test
    @Order(8)
    void assignJob_NonOpenJob_ShouldFail() {
        User anotherWorker = UserFactory.createWorkerUser(
                "Another", "Worker", "anotherworker@test.com", "Password123!", "0829876543"
        );
        anotherWorker = userService.create(anotherWorker);

        User finalAnotherWorker = anotherWorker;
        assertThrows(IllegalStateException.class, () -> {
            jobService.assignJob(testJob.getJobId(), finalAnotherWorker);
        });
        System.out.println("Correctly prevented assignment of non-open job");
    }

    @Test
    @Order(9)
    void completeJob() {
        testJob = jobService.completeJob(testJob.getJobId());
        assertEquals(Job.JobStatus.COMPLETED, testJob.getStatus());
        System.out.println("Completed job");
    }

    @Test
    @Order(10)
    void cancelJob() {
        secondJob = jobService.cancelJob(secondJob.getJobId());
        assertEquals(Job.JobStatus.CANCELLED, secondJob.getStatus());
        System.out.println("Cancelled job");
    }

    @Test
    @Order(11)
    void findOpenJobs() {
        // Create another open job for testing
        Job openJob = jobService.postJob(
                clientUser,
                "Graphic Design Work",
                "Need a logo and branding materials",
                "Design",
                2000.0,
                "Durban"
        );

        List<Job> openJobs = jobService.findOpenJobs();
        assertFalse(openJobs.isEmpty());
        assertTrue(openJobs.stream()
                .allMatch(job -> job.getStatus() == Job.JobStatus.OPEN));
        System.out.println("Found open jobs: " + openJobs.size());
    }

    @Test
    @Order(12)
    void findJobsByClient() {
        List<Job> clientJobs = jobService.findJobsByClient(clientUser);
        assertFalse(clientJobs.isEmpty());
        assertTrue(clientJobs.stream()
                .allMatch(job -> job.getClient().getUserId().equals(clientUser.getUserId())));
        System.out.println("Found jobs for client: " + clientJobs.size());
    }

    @Test
    @Order(13)
    void findJobsByCategory() {
        List<Job> techJobs = jobService.findJobsByCategory("Technology");
        assertFalse(techJobs.isEmpty());
        assertTrue(techJobs.stream()
                .allMatch(job -> job.getCategory().equals("Technology")));
        System.out.println("Found Technology jobs: " + techJobs.size());
    }

    @Test
    @Order(14)
    void findJobsByLocation() {
        List<Job> capeTownJobs = jobService.findJobsByLocation("Cape Town");
        assertFalse(capeTownJobs.isEmpty());
        assertTrue(capeTownJobs.stream()
                .allMatch(job -> job.getLocation().equals("Cape Town")));
        System.out.println("Found Cape Town jobs: " + capeTownJobs.size());
    }

    @Test
    @Order(15)
    void searchJobs() {
        List<Job> webJobs = jobService.searchJobs("web");
        assertFalse(webJobs.isEmpty());
        System.out.println("Found jobs containing 'web': " + webJobs.size());
    }

    @Test
    @Order(16)
    void findJobsByBudgetRange() {
        List<Job> jobsInRange = jobService.findJobsByBudgetRange(1000.0, 6000.0);
        assertFalse(jobsInRange.isEmpty());
        assertTrue(jobsInRange.stream()
                .allMatch(job -> job.getBudget() >= 1000.0 && job.getBudget() <= 6000.0));
        System.out.println("Found jobs in budget range: " + jobsInRange.size());
    }

    @Test
    @Order(17)
    void findRecentJobs() {
        List<Job> recentJobs = jobService.findRecentJobs(7);
        assertFalse(recentJobs.isEmpty());
        System.out.println("Found recent jobs (7 days): " + recentJobs.size());
    }

    @Test
    @Order(18)
    void countJobsByStatus() {
        Long openJobsCount = jobService.countJobsByStatus(Job.JobStatus.OPEN);
        Long completedJobsCount = jobService.countJobsByStatus(Job.JobStatus.COMPLETED);
        Long cancelledJobsCount = jobService.countJobsByStatus(Job.JobStatus.CANCELLED);

        assertTrue(openJobsCount >= 0);
        assertTrue(completedJobsCount >= 0);
        assertTrue(cancelledJobsCount >= 0);

        System.out.println("Open jobs: " + openJobsCount);
        System.out.println("Completed jobs: " + completedJobsCount);
        System.out.println("Cancelled jobs: " + cancelledJobsCount);
    }

    @Test
    @Order(19)
    void delete() {
        Job deletedJob = jobService.delete(testJob.getJobId());
        assertNotNull(deletedJob);
        assertEquals(testJob.getJobId(), deletedJob.getJobId());

        assertThrows(RuntimeException.class, () -> {
            jobService.read(testJob.getJobId());
        });
        System.out.println("Deleted job: " + deletedJob.getJobId());
    }
}