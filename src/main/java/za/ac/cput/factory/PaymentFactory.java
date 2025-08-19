package za.ac.cput.factory;

import za.ac.cput.domain.*;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;

public class PaymentFactory {

    public static Payment createPayment(Contract contract, Double amount, Payment.PaymentMethod method) {

        // Validate inputs
        if (contract == null) {
            return null;
        }
        if (!ValidationHelper.isValidBudget(amount)) {
            return null;
        }
        if (method == null) {
            return null;
        }

        return new Payment.Builder()
                .setPaymentId(ValidationHelper.generateId())
                .setContract(contract)
                .setAmount(amount)
                .setMethod(method)
                .setStatus(Payment.PaymentStatus.PENDING)
                .setDateProcessed(LocalDateTime.now())
                .build();
    }

    public static Payment createEscrowPayment(Contract contract, Double amount) {
        Payment payment = createPayment(contract, amount, Payment.PaymentMethod.CARD);
        if (payment == null) return null;

        return new Payment.Builder()
                .copy(payment)
                .setStatus(Payment.PaymentStatus.ESCROW)
                .build();
    }
}
