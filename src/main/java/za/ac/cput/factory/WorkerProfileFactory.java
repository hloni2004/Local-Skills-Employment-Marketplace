package za.ac.cput.factory;

import za.ac.cput.domain.*;
import za.ac.cput.domain.User;
import za.ac.cput.domain.WorkerProfile;
import za.ac.cput.util.ValidationHelper;

import java.util.List;

public class WorkerProfileFactory {

    public static WorkerProfile createWorkerProfile(User user, String bio, List<Skill> skills,
                                                    String experience, Double hourlyRate, String location) {


        if (user == null) {
            return null;
        }
        if (bio != null && !ValidationHelper.isValidLength(bio, 10, 1000)) {
            return null;
        }
        if (skills == null || skills.isEmpty()) {
            return null;
        }
        if (!ValidationHelper.isValidHourlyRate(hourlyRate)) {
            return null;
        }
        if (ValidationHelper.isNullOrEmpty(location)) {
            return null;
        }

        return new WorkerProfile.Builder()
                .setProfileId(ValidationHelper.generateId())
                .setUser(user)
                .setBio(bio != null ? bio.trim() : null)
                .setSkills(skills)
                .setExperience(experience != null ? experience.trim() : null)
                .setHourlyRate(hourlyRate)
                .setAvailabilityStatus(WorkerProfile.AvailabilityStatus.AVAILABLE)
                .setLocation(location.trim())
                .setRating(null) // New profiles start with no rating
                .setVerificationStatus(WorkerProfile.VerificationStatus.PENDING)
                .setVerificationCode(null)
                .build();
    }

    public static WorkerProfile createBasicWorkerProfile(User user, List<Skill> skills,
                                                         Double hourlyRate, String location) {
        return createWorkerProfile(user, null, skills, null, hourlyRate, location);
    }
}