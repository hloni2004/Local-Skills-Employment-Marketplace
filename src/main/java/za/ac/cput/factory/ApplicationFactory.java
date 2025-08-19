package za.ac.cput.factory;

import za.ac.cput.domain.Application;
import za.ac.cput.domain.*;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;

public class ApplicationFactory {

    public static Application createApplication(Job job, User worker, String coverLetter, Double expectedPay) {

        // Validate inputs
        if (job == null || worker == null) {
            return null;
        }
        if (coverLetter != null && !ValidationHelper.isValidLength(coverLetter, 20, 1000)) {
            return null;
        }
        if (expectedPay != null && !ValidationHelper.isValidBudget(expectedPay)) {
            return null;
        }

        return new Application.Builder()
                .setApplicationId(ValidationHelper.generateId())
                .setJob(job)
                .setWorker(worker)
                .setCoverLetter(coverLetter != null ? coverLetter.trim() : null)
                .setExpectedPay(expectedPay)
                .setStatus(Application.ApplicationStatus.PENDING)
                .setDateApplied(LocalDateTime.now())
                .build();
    }

    public static Application createQuickApplication(Job job, User worker, Double expectedPay) {
        return createApplication(job, worker, null, expectedPay);
    }
}