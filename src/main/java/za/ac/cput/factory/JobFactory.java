package za.ac.cput.factory;

import za.ac.cput.domain.*;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;

public class JobFactory {

    public static Job createJob(User client, String title, String description, String category,
                                Double budget, String location) {


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
        Job job = createJob(client, title, description, category, budget, location);
        if (job == null) return null;


        return job;
    }
}
