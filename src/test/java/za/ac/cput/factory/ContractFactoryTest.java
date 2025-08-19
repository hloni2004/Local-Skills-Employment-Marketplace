package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.*;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ContractFactoryTest {

    private Job job;
    private User client;
    private User worker;
    private WorkerProfile workerProfile;

    @BeforeEach
    void setUp() {
        // Sample Job
        job = new Job.Builder()
                .setJobId("J101")
                .setTitle("Backend Developer")
                .setDescription("Develop REST API with Spring Boot")
                .setBudget(8000.0)
                .setPostedDate(LocalDateTime.now())
                .setDeadline(LocalDateTime.now().plusDays(10))
                .setStatus(Job.JobStatus.OPEN)
                .setClient(null)
                .setContracts(null)
                .build();

        // Sample Client
        client = new User.Builder()
                .setUserId("U201")
                .setFirstName("Alice")
                .setLastName("Mokoena")
                .setEmail("alice@example.com")
                .setPassword("password123")
                .setRole(User.UserRole.CLIENT)
                .setJobs(null)
                .setContracts(null)
                .setReviews(null)
                .setVerifications(null)
                .build();

        // Sample Worker
        worker = new User.Builder()
                .setUserId("U202")
                .setFirstName("Bob")
                .setLastName("Smith")
                .setEmail("bob@example.com")
                .setPassword("secure456")
                .setRole(User.UserRole.WORKER)
                .setJobs(null)
                .setContracts(null)
                .setReviews(null)
                .setVerifications(null)
                .build();

        // WorkerProfile for the worker
        workerProfile = new WorkerProfile.Builder()
                .setProfileId("P301")
                .setUser(worker)
                .setBio("Full-stack developer with 6 years experience")
                .setSkills(Collections.emptyList())
                .setExperience("6 years in full-stack development")
                .setHourlyRate(500.0)
                .setAvailabilityStatus(WorkerProfile.AvailabilityStatus.AVAILABLE)
                .setLocation("Cape Town")
                .setRating(4.8)
                .setVerificationStatus(WorkerProfile.VerificationStatus.VERIFIED)
                .setVerificationCode("VER123")
                .build();
    }

    @Test
    void testCreateValidContract() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(5);

        Contract contract = ContractFactory.createContract(
                job, client, worker, startDate, endDate, 5000.0, "Standard terms"
        );

        assertNotNull(contract);
        assertEquals(job, contract.getJob());
        assertEquals(client, contract.getClient());
        assertEquals(worker, contract.getWorker());
        assertEquals(5000.0, contract.getAgreedPay());
        assertEquals(Contract.ContractStatus.ACTIVE, contract.getStatus());
        assertEquals("Standard terms", contract.getTerms());
        assertNotNull(contract.getContractId());
        assertNotNull(contract.getPayments());
        assertNotNull(contract.getReviews());
    }

    @Test
    void testCreateContractWithWorkerProfile() {
        LocalDateTime startDate = LocalDateTime.now();

        Contract contract = ContractFactory.createContract(
                job, client, worker, startDate, null, 4000.0, "Profiled worker terms"
        );

        assertNotNull(contract);
        assertEquals(worker, contract.getWorker());
        assertEquals("Bob", contract.getWorker().getFirstName());
        assertEquals("Full-stack developer with 6 years experience", workerProfile.getBio());
        assertEquals(WorkerProfile.AvailabilityStatus.AVAILABLE, workerProfile.getAvailabilityStatus());
    }

    @Test
    void testCreateContractWithNullWorker() {
        Contract contract = ContractFactory.createContract(job, client, null, LocalDateTime.now(), null, 2000.0, "Terms");
        assertNull(contract);
    }

    @Test
    void testCreateContractWithInvalidPay() {
        Contract contract = ContractFactory.createContract(job, client, worker, LocalDateTime.now(), null, -1000.0, "Terms");
        assertNull(contract);
    }

    @Test
    void testCreateImmediateContract() {
        Contract contract = ContractFactory.createImmediateContract(job, client, worker, 2500.0, "Immediate terms");

        assertNotNull(contract);
        assertEquals(job, contract.getJob());
        assertEquals(client, contract.getClient());
        assertEquals(worker, contract.getWorker());
        assertEquals(2500.0, contract.getAgreedPay());
        assertEquals(Contract.ContractStatus.ACTIVE, contract.getStatus());
        assertNotNull(contract.getStartDate());
        assertNull(contract.getEndDate());
    }
}
