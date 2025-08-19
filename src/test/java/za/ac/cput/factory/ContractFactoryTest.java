package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Contract;
import za.ac.cput.domain.Job;
import za.ac.cput.domain.User;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ContractFactoryTest {

    private Job job;
    private User client;
    private User worker;

    @BeforeEach
    void setUp() {
        client = UserFactory.createClientUser("Client", "User", "client@email.com", "Password123!");
        worker = UserFactory.createWorkerUser("Worker", "User", "worker@email.com", "Password123!", "0821234567");
        job = JobFactory.createJob(client, "Software Development",
                "Need a software developer for mobile app", "Programming", 25000.0, "Cape Town");
    }

    @Test
    void createContract() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(30);

        Contract contract = ContractFactory.createContract(job, client, worker, startDate, endDate, 20000.0);

        assertNotNull(contract);
        assertEquals(job, contract.getJob());
        assertEquals(client, contract.getClient());
        assertEquals(worker, contract.getWorker());
        assertEquals(startDate, contract.getStartDate());
        assertEquals(endDate, contract.getEndDate());
        assertEquals(20000.0, contract.getAgreedPay());
        assertEquals(Contract.ContractStatus.ACTIVE, contract.getStatus());
        assertNotNull(contract.getContractId());
    }

    @Test
    void createContract_WithoutEndDate() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);

        Contract contract = ContractFactory.createContract(job, client, worker, startDate, null, 20000.0);

        assertNotNull(contract);
        assertEquals(startDate, contract.getStartDate());
        assertNull(contract.getEndDate());
    }

    @Test
    void createContract_InvalidJob() {
        Contract contract = ContractFactory.createContract(null, client, worker,
                LocalDateTime.now().plusDays(1), null, 20000.0);
        assertNull(contract);
    }

    @Test
    void createContract_InvalidClient() {
        Contract contract = ContractFactory.createContract(job, null, worker,
                LocalDateTime.now().plusDays(1), null, 20000.0);
        assertNull(contract);
    }

    @Test
    void createContract_InvalidWorker() {
        Contract contract = ContractFactory.createContract(job, client, null,
                LocalDateTime.now().plusDays(1), null, 20000.0);
        assertNull(contract);
    }

    @Test
    void createContract_InvalidStartDate() {
        Contract contract = ContractFactory.createContract(job, client, worker, null, null, 20000.0);
        assertNull(contract);
    }

    @Test
    void createContract_EndDateBeforeStartDate() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(10);
        LocalDateTime endDate = LocalDateTime.now().plusDays(5);

        Contract contract = ContractFactory.createContract(job, client, worker, startDate, endDate, 20000.0);
        assertNull(contract);
    }

    @Test
    void createContract_InvalidAgreedPay() {
        // Test with null agreed pay
        Contract contract = ContractFactory.createContract(job, client, worker,
                LocalDateTime.now().plusDays(1), null, null);
        assertNull(contract);

        // Test with negative agreed pay
        contract = ContractFactory.createContract(job, client, worker,
                LocalDateTime.now().plusDays(1), null, -1000.0);
        assertNull(contract);

        // Test with zero agreed pay
        contract = ContractFactory.createContract(job, client, worker,
                LocalDateTime.now().plusDays(1), null, 0.0);
        assertNull(contract);
    }

    @Test
    void createImmediateContract() {
        Contract contract = ContractFactory.createImmediateContract(job, client, worker, 15000.0);

        assertNotNull(contract);
        assertEquals(job, contract.getJob());
        assertEquals(client, contract.getClient());
        assertEquals(worker, contract.getWorker());
        assertEquals(15000.0, contract.getAgreedPay());
        assertNull(contract.getEndDate());
        assertNotNull(contract.getStartDate());
    }

    @Test
    void createImmediateContract_InvalidInput() {
        Contract contract = ContractFactory.createImmediateContract(null, client, worker, 15000.0);
        assertNull(contract);
    }
}
