package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Job;
import za.ac.cput.domain.User;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JobFactoryTest {

    private User client;

    @BeforeEach
    void setUp() {
        client = UserFactory.createClientUser("John", "Doe", "john.doe@email.com", "Password123!");
    }

    @Test
    void createJob() {
        Job job = JobFactory.createJob(client, "Web Developer",
                "Looking for a skilled web developer to build an e-commerce website",
                "Web Development", 15000.0, "Cape Town");

        assertNotNull(job);
        assertEquals(client, job.getClient());
        assertEquals("Web Developer", job.getTitle());
        assertEquals("Looking for a skilled web developer to build an e-commerce website", job.getDescription());
        assertEquals("Web Development", job.getCategory());
        assertEquals(15000.0, job.getBudget());
        assertEquals("Cape Town", job.getLocation());
        assertEquals(Job.JobStatus.OPEN, job.getStatus());
        assertNotNull(job.getJobId());
        assertNotNull(job.getDatePosted());
        System.out.println(job);
    }

    @Test
    void createJob_InvalidClient() {
        Job job = JobFactory.createJob(null, "Web Developer",
                "Looking for a skilled web developer", "Web Development", 15000.0, "Cape Town");
        assertNull(job);
        System.out.println(job);
    }

    @Test
    void createJob_InvalidTitle() {
        // Test with null title
        Job job = JobFactory.createJob(client, null,
                "Looking for a skilled web developer", "Web Development", 15000.0, "Cape Town");
        assertNull(job);

        // Test with title too short
        job = JobFactory.createJob(client, "Web",
                "Looking for a skilled web developer", "Web Development", 15000.0, "Cape Town");
        assertNull(job);

        // Test with title too long
        job = JobFactory.createJob(client, "A".repeat(101),
                "Looking for a skilled web developer", "Web Development", 15000.0, "Cape Town");
        assertNull(job);
        System.out.println(job);
    }

    @Test
    void createJob_InvalidDescription() {
        // Test with null description
        Job job = JobFactory.createJob(client, "Web Developer", null,
                "Web Development", 15000.0, "Cape Town");
        assertNull(job);

        // Test with description too short
        job = JobFactory.createJob(client, "Web Developer", "Short desc",
                "Web Development", 15000.0, "Cape Town");
        assertNull(job);

        // Test with description too long
        job = JobFactory.createJob(client, "Web Developer", "A".repeat(2001),
                "Web Development", 15000.0, "Cape Town");
        assertNull(job);
        System.out.println(job);
    }

    @Test
    void createJob_InvalidBudget() {
        // Test with null budget
        Job job = JobFactory.createJob(client, "Web Developer",
                "Looking for a skilled web developer", "Web Development", null, "Cape Town");
        assertNull(job);

        // Test with negative budget
        job = JobFactory.createJob(client, "Web Developer",
                "Looking for a skilled web developer", "Web Development", -1000.0, "Cape Town");
        assertNull(job);

        // Test with zero budget
        job = JobFactory.createJob(client, "Web Developer",
                "Looking for a skilled web developer", "Web Development", 0.0, "Cape Town");
        assertNull(job);
        System.out.println(job);
    }

    @Test
    void createJob_InvalidLocation() {
        Job job = JobFactory.createJob(client, "Web Developer",
                "Looking for a skilled web developer", "Web Development", 15000.0, null);
        assertNull(job);

        job = JobFactory.createJob(client, "Web Developer",
                "Looking for a skilled web developer", "Web Development", 15000.0, "");
        assertNull(job);
        System.out.println(job);
    }

    @Test
    void createUrgentJob() {
        Job job = JobFactory.createUrgentJob(client, "Urgent Plumbing Repair",
                "Need immediate plumbing repair for burst pipe", "Plumbing", 2000.0, "Johannesburg");

        assertNotNull(job);
        assertEquals("Urgent Plumbing Repair", job.getTitle());
        assertEquals("Need immediate plumbing repair for burst pipe", job.getDescription());
        assertEquals(Job.JobStatus.OPEN, job.getStatus());
        System.out.println(job);
    }

    @Test
    void createUrgentJob_InvalidInput() {
        Job job = JobFactory.createUrgentJob(null, "Urgent Job",
                "This is an urgent job description", "Category", 1000.0, "Location");
        assertNull(job);
        System.out.println(job);
    }
}