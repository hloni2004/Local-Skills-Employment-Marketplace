package za.ac.cput.factory;

import za.ac.cput.domain.*;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;

public class ContractFactory {

    public static Contract createContract(Job job, User client, User worker, LocalDateTime startDate,
                                          LocalDateTime endDate, Double agreedPay) {

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
                .build();
    }

    public static Contract createImmediateContract(Job job, User client, User worker, Double agreedPay) {
        return createContract(job, client, worker, LocalDateTime.now(), null, agreedPay);
    }
}