package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Contract;
import za.ac.cput.domain.Dispute;
import za.ac.cput.domain.Job;
import za.ac.cput.domain.User;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DisputeFactoryTest {

    private Contract contract;
    private User client;
    private User worker;

    @BeforeEach
    void setUp() {
        client = UserFactory.createClientUser("Client", "User", "client@email.com", "Password123!");
        worker = UserFactory.createWorkerUser("Worker", "User", "worker@email.com", "Password123!", "0821234567");
        Job job = JobFactory.createJob(client, "Test Job",
                "This is a test job for dispute testing", "Testing", 5000.0, "Cape Town");
        contract = ContractFactory.createContract(job, client, worker,
                LocalDateTime.now(), LocalDateTime.now().plusDays(7), 4500.0);
    }

    @Test
    void createDispute() {
        String reason = "The work delivered does not meet the agreed specifications and quality standards.";
        Dispute dispute = DisputeFactory.createDispute(contract, client, reason);

        assertNotNull(dispute);
        assertEquals(contract, dispute.getContract());
        assertEquals(client, dispute.getOpenedBy());
        assertEquals(reason, dispute.getReason());
        assertEquals(Dispute.DisputeStatus.OPEN, dispute.getStatus());
        assertNull(dispute.getResolutionDetails());
        assertNotNull(dispute.getDisputeId());
        assertNotNull(dispute.getDateOpened());
        System.out.println(dispute);
    }

    @Test
    void createDispute_InvalidContract() {
        String reason = "Valid reason for opening a dispute against the contractor.";
        Dispute dispute = DisputeFactory.createDispute(null, client, reason);
        assertNull(dispute);
        System.out.println(reason);
    }

    @Test
    void createDispute_InvalidOpenedBy() {
        String reason = "Valid reason for opening a dispute against the contractor.";
        Dispute dispute = DisputeFactory.createDispute(contract, null, reason);
        assertNull(dispute);
    }

    @Test
    void createDispute_InvalidReason() {
        // Test with null reason
        Dispute dispute = DisputeFactory.createDispute(contract, client, null);
        assertNull(dispute);

        // Test with empty reason
        dispute = DisputeFactory.createDispute(contract, client, "");
        assertNull(dispute);

        // Test with reason too short
        dispute = DisputeFactory.createDispute(contract, client, "Too short");
        assertNull(dispute);

        // Test with reason too long
        dispute = DisputeFactory.createDispute(contract, client, "A".repeat(1001));
        assertNull(dispute);
    }

    @Test
    void createDispute_ByWorker() {
        String reason = "Client has not provided necessary resources and keeps changing requirements.";
        Dispute dispute = DisputeFactory.createDispute(contract, worker, reason);

        assertNotNull(dispute);
        assertEquals(worker, dispute.getOpenedBy());
        assertEquals(reason, dispute.getReason());
        System.out.println(dispute);
    }
}
