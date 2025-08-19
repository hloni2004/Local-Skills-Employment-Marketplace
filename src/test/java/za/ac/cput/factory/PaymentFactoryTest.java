package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Contract;
import za.ac.cput.domain.Job;
import za.ac.cput.domain.Payment;
import za.ac.cput.domain.User;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PaymentFactoryTest {

    private Contract contract;

    @BeforeEach
    void setUp() {
        User client = UserFactory.createClientUser("Client", "User", "client@email.com", "Password123!");
        User worker = UserFactory.createWorkerUser("Worker", "User", "worker@email.com", "Password123!", "0821234567");
        Job job = JobFactory.createJob(client, "Payment Test Job",
                "Job for testing payment functionality", "Testing", 8000.0, "Cape Town");
        contract = ContractFactory.createContract(job, client, worker,
                LocalDateTime.now(), LocalDateTime.now().plusDays(14), 7500.0);
    }

    @Test
    void createPayment() {
        Payment payment = PaymentFactory.createPayment(contract, 7500.0, Payment.PaymentMethod.CARD);

        assertNotNull(payment);
        assertEquals(contract, payment.getContract());
        assertEquals(7500.0, payment.getAmount());
        assertEquals(Payment.PaymentMethod.CARD, payment.getMethod());
        assertEquals(Payment.PaymentStatus.PENDING, payment.getStatus());
        assertNotNull(payment.getPaymentId());
        assertNotNull(payment.getDateProcessed());
        System.out.println(payment);
    }

    @Test
    void createPayment_DifferentMethods() {
        Payment cardPayment = PaymentFactory.createPayment(contract, 5000.0, Payment.PaymentMethod.CARD);
        assertNotNull(cardPayment);
        assertEquals(Payment.PaymentMethod.CARD, cardPayment.getMethod());

        Payment mobilePayment = PaymentFactory.createPayment(contract, 3000.0, Payment.PaymentMethod.MOBILE_MONEY);
        assertNotNull(mobilePayment);
        assertEquals(Payment.PaymentMethod.MOBILE_MONEY, mobilePayment.getMethod());

        Payment eftPayment = PaymentFactory.createPayment(contract, 2000.0, Payment.PaymentMethod.EFT);
        assertNotNull(eftPayment);
        assertEquals(Payment.PaymentMethod.EFT, eftPayment.getMethod());

        Payment cashPayment = PaymentFactory.createPayment(contract, 1500.0, Payment.PaymentMethod.CASH);
        assertNotNull(cashPayment);
        assertEquals(Payment.PaymentMethod.CASH, cashPayment.getMethod());
    }

    @Test
    void createPayment_InvalidContract() {
        Payment payment = PaymentFactory.createPayment(null, 5000.0, Payment.PaymentMethod.CARD);
        assertNull(payment);
    }

    @Test
    void createPayment_InvalidAmount() {
        // Test with null amount
        Payment payment = PaymentFactory.createPayment(contract, null, Payment.PaymentMethod.CARD);
        assertNull(payment);

        // Test with negative amount
        payment = PaymentFactory.createPayment(contract, -1000.0, Payment.PaymentMethod.CARD);
        assertNull(payment);

        // Test with zero amount
        payment = PaymentFactory.createPayment(contract, 0.0, Payment.PaymentMethod.CARD);
        assertNull(payment);
    }

    @Test
    void createPayment_InvalidMethod() {
        Payment payment = PaymentFactory.createPayment(contract, 5000.0, null);
        assertNull(payment);
    }

    @Test
    void createEscrowPayment() {
        Payment payment = PaymentFactory.createEscrowPayment(contract, 6000.0);

        assertNotNull(payment);
        assertEquals(contract, payment.getContract());
        assertEquals(6000.0, payment.getAmount());
        assertEquals(Payment.PaymentMethod.CARD, payment.getMethod());
        assertEquals(Payment.PaymentStatus.ESCROW, payment.getStatus());
        assertNotNull(payment.getPaymentId());
        assertNotNull(payment.getDateProcessed());
        System.out.println(payment);
    }

    @Test
    void createEscrowPayment_InvalidInput() {
        Payment payment = PaymentFactory.createEscrowPayment(null, 5000.0);
        assertNull(payment);
    }
}