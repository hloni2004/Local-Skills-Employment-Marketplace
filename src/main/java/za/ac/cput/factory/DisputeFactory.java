package za.ac.cput.factory;

import za.ac.cput.domain.*;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;

public class DisputeFactory {

    public static Dispute createDispute(Contract contract, User openedBy, String reason) {

        // Validate inputs
        if (contract == null || openedBy == null) {
            return null;
        }
        if (ValidationHelper.isNullOrEmpty(reason) || !ValidationHelper.isValidLength(reason, 20, 1000)) {
            return null;
        }

        return new Dispute.Builder()
                .setDisputeId(ValidationHelper.generateId())
                .setContract(contract)
                .setOpenedBy(openedBy)
                .setReason(reason.trim())
                .setStatus(Dispute.DisputeStatus.OPEN)
                .setResolutionDetails(null)
                .setDateOpened(LocalDateTime.now())
                .build();
    }
}