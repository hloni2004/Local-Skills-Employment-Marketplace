package za.ac.cput.factory;

import za.ac.cput.domain.*;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;

public class JobFactory {

    public static Job createJob(User client, String title, String description, String category,
                                Double budget, String location) {

        // Validate inputs
        if (client == null) {
            return null;
        }
        if (ValidationHelper.isNullOrEmpty(title) || !ValidationHelper.isValidLength(title, 5, 100)) {
            return null;
        }
        if (ValidationHelper.isNullOrEmpty(description) || !ValidationHelper.isValidLength(description, 20, 2000)) {
            return null;
        }
        if (ValidationHelper.isNullOrEmpty(category)) {
            return null;
        }
        if (!ValidationHelper.isValidBudget(budget)) {
            return null;
        }
        if (ValidationHelper.isNullOrEmpty(location)) {
            return null;
        }

        return new Job.Builder()
                .setJobId(ValidationHelper.generateId())
                .setClient(client)
                .setTitle(title.trim())
                .setDescription(description.trim())
                .setCategory(category.trim())
                .setBudget(budget)
                .setLocation(location.trim())
                .setDatePosted(LocalDateTime.now())
                .setStatus(Job.JobStatus.OPEN)
                .build();
    }

    public static Job createUrgentJob(User client, String title, String description, String category,
                                      Double budget, String location) {
        // Create regular job first to validate inputs
        Job job = createJob(client, title, description, category, budget, location);
        if (job == null) return null;

        // For urgent jobs, we could add special handling or priority flags here
        // For now, it's the same as regular job creation
        return job;
    }
}