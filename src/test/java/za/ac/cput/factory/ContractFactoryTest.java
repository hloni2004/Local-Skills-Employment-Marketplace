package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ContractFactoryTest {

    private Job job;
    private User client;
    private User worker;

    @BeforeEach
    void setUp() {
        // Create client user
        client = UserFactory.createClientUser("Alice", "Mokoena", "alice@example.com", "Password123!");

        // Create worker user
        worker = UserFactory.createWorkerUser("Bob", "Smith", "bob@example.com", "Password123!", "0821234567");

        // Create job
        job = JobFactory.createJob(client, "Backend Developer",
                "Develop REST API with Spring Boot", "Programming", 8000.0, "Cape Town");
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
        assertEquals(startDate, contract.getStartDate());
        assertEquals(endDate, contract.getEndDate());
        System.out.println(contract);
    }

    @Test
    void testCreateContractWithoutEndDate() {
        LocalDateTime startDate = LocalDateTime.now();

        Contract contract = ContractFactory.createContract(
                job, client, worker, startDate, null, 4000.0, "No end date terms"
        );

        assertNotNull(contract);
        assertEquals(worker, contract.getWorker());
        assertEquals("Bob", contract.getWorker().getFirstName());
        assertNull(contract.getEndDate());
        System.out.println(contract);
    }

    @Test
    void testCreateContractWithNullJob() {
        Contract contract = ContractFactory.createContract(
                null, client, worker, LocalDateTime.now(), null, 2000.0, "Terms");
        assertNull(contract);
    }

    @Test
    void testCreateContractWithNullClient() {
        Contract contract = ContractFactory.createContract(
                job, null, worker, LocalDateTime.now(), null, 2000.0, "Terms");
        assertNull(contract);
    }

    @Test
    void testCreateContractWithNullWorker() {
        Contract contract = ContractFactory.createContract(
                job, client, null, LocalDateTime.now(), null, 2000.0, "Terms");
        assertNull(contract);
    }

    @Test
    void testCreateContractWithInvalidPay() {
        Contract contract = ContractFactory.createContract(
                job, client, worker, LocalDateTime.now(), null, -1000.0, "Terms");
        assertNull(contract);

        contract = ContractFactory.createContract(
                job, client, worker, LocalDateTime.now(), null, 0.0, "Terms");
        assertNull(contract);
    }

    @Test
    void testCreateContractWithInvalidDates() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.minusDays(1); // End before start

        Contract contract = ContractFactory.createContract(
                job, client, worker, startDate, endDate, 5000.0, "Terms");
        assertNull(contract);
    }

    @Test
    void testCreateImmediateContract() {
        Contract contract = ContractFactory.createImmediateContract(
                job, client, worker, 2500.0, "Immediate terms");

        assertNotNull(contract);
        assertEquals(job, contract.getJob());
        assertEquals(client, contract.getClient());
        assertEquals(worker, contract.getWorker());
        assertEquals(2500.0, contract.getAgreedPay());
        assertEquals(Contract.ContractStatus.ACTIVE, contract.getStatus());
        assertNotNull(contract.getStartDate());
        assertNull(contract.getEndDate());
        System.out.println(contract);
    }
}