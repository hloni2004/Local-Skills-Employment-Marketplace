package za.ac.cput.factory;

import za.ac.cput.domain.*;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;
import java.util.Collections;

public class ContractFactory {

    public static Contract createContract(Job job, User client, User worker, LocalDateTime startDate,
                                          LocalDateTime endDate, Double agreedPay, String terms) {

        // Validate inputs
        if (job == null || client == null || worker == null) {
            return null;
        }
        if (startDate == null) {
            return null;
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            return null;
        }
        if (!ValidationHelper.isValidBudget(agreedPay)) {
            return null;
        }

        return new Contract.Builder()
                .setContractId(ValidationHelper.generateId())
                .setJob(job)
                .setClient(client)
                .setWorker(worker)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setAgreedPay(agreedPay)
                .setStatus(Contract.ContractStatus.ACTIVE)
                .setTerms(terms)
                .setPayments(Collections.emptyList()) // initialize empty list
                .setReviews(Collections.emptyList())  // initialize empty list
                .build();
    }

    public static Contract createImmediateContract(Job job, User client, User worker, Double agreedPay, String terms) {
        return createContract(job, client, worker, LocalDateTime.now(), null, agreedPay, terms);
    }
}
