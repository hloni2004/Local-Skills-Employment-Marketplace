package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Skill;
import za.ac.cput.domain.User;
import za.ac.cput.domain.WorkerProfile;
import za.ac.cput.repository.WorkerProfileRepository;
import za.ac.cput.factory.WorkerProfileFactory;
import za.ac.cput.util.ValidationHelper;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WorkerProfileService implements IService<WorkerProfile, String> {

    private final WorkerProfileRepository workerProfileRepository;
    private final ReviewService reviewService;
    private final NotificationService notificationService;

    @Autowired
    public WorkerProfileService(WorkerProfileRepository workerProfileRepository,
                                ReviewService reviewService,
                                NotificationService notificationService) {
        this.workerProfileRepository = workerProfileRepository;
        this.reviewService = reviewService;
        this.notificationService = notificationService;
    }

    @Override
    public WorkerProfile create(WorkerProfile workerProfile) {
        ValidationHelper.ValidationResult result = ValidationHelper.validateWorkerProfile(workerProfile);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid worker profile data: " + result.getErrorMessage());
        }

        // Check if user already has a worker profile
        if (workerProfileRepository.findByUser(workerProfile.getUser()).isPresent()) {
            throw new IllegalStateException("User already has a worker profile");
        }

        WorkerProfile savedProfile = workerProfileRepository.save(workerProfile);

        // Notify user that profile was created
        notificationService.createSystemNotification(
                workerProfile.getUser(),
                "Your worker profile has been created and is pending verification"
        );

        return savedProfile;
    }

    @Override
    public WorkerProfile read(String profileId) {
        return workerProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Worker profile not found with ID: " + profileId));
    }

    @Override
    public WorkerProfile update(WorkerProfile workerProfile) {
        if (!workerProfileRepository.existsById(workerProfile.getProfileId())) {
            throw new RuntimeException("Worker profile not found");
        }

        ValidationHelper.ValidationResult result = ValidationHelper.validateWorkerProfile(workerProfile);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid worker profile data: " + result.getErrorMessage());
        }

        return workerProfileRepository.save(workerProfile);
    }

    @Override
    public WorkerProfile delete(String profileId) {
        WorkerProfile profile = read(profileId);
        workerProfileRepository.deleteById(profileId);
        return profile;
    }

    // Business Logic Methods
    public WorkerProfile createWorkerProfile(User user, String bio, List<Skill> skills,
                                             String experience, Double hourlyRate, String location) {
        WorkerProfile profile = WorkerProfileFactory.createWorkerProfile(
                user, bio, skills, experience, hourlyRate, location);
        if (profile == null) {
            throw new IllegalArgumentException("Invalid worker profile creation data");
        }
        return create(profile);
    }

    public WorkerProfile createBasicWorkerProfile(User user, List<Skill> skills,
                                                  Double hourlyRate, String location) {
        WorkerProfile profile = WorkerProfileFactory.createBasicWorkerProfile(
                user, skills, hourlyRate, location);
        if (profile == null) {
            throw new IllegalArgumentException("Invalid worker profile creation data");
        }
        return create(profile);
    }

    public Optional<WorkerProfile> findByUser(User user) {
        return workerProfileRepository.findByUser(user);
    }

    public WorkerProfile verifyProfile(String profileId, String verificationCode) {
        WorkerProfile profile = read(profileId);

        if (profile.getVerificationStatus() == WorkerProfile.VerificationStatus.VERIFIED) {
            throw new IllegalStateException("Profile is already verified");
        }

        WorkerProfile verifiedProfile = update(new WorkerProfile.Builder()
                .copy(profile)
                .setVerificationStatus(WorkerProfile.VerificationStatus.VERIFIED)
                .setVerificationCode(verificationCode)
                .build());

        // Notify user of verification
        notificationService.createSystemNotification(
                profile.getUser(),
                "Your worker profile has been verified! You can now receive job offers."
        );

        return verifiedProfile;
    }

    public WorkerProfile rejectProfile(String profileId, String reason) {
        WorkerProfile profile = read(profileId);

        return update(new WorkerProfile.Builder()
                .copy(profile)
                .setVerificationStatus(WorkerProfile.VerificationStatus.REJECTED)
                .build());
    }

    public WorkerProfile updateAvailabilityStatus(String profileId,
                                                  WorkerProfile.AvailabilityStatus status) {
        WorkerProfile profile = read(profileId);

        return update(new WorkerProfile.Builder()
                .copy(profile)
                .setAvailabilityStatus(status)
                .build());
    }

    public WorkerProfile setAvailable(String profileId) {
        return updateAvailabilityStatus(profileId, WorkerProfile.AvailabilityStatus.AVAILABLE);
    }

    public WorkerProfile setBusy(String profileId) {
        return updateAvailabilityStatus(profileId, WorkerProfile.AvailabilityStatus.BUSY);
    }

    public WorkerProfile setOffline(String profileId) {
        return updateAvailabilityStatus(profileId, WorkerProfile.AvailabilityStatus.OFFLINE);
    }

    public WorkerProfile updateRating(String profileId, Double newRating) {
        WorkerProfile profile = read(profileId);

        return update(new WorkerProfile.Builder()
                .copy(profile)
                .setRating(newRating)
                .build());
    }

    public WorkerProfile addSkill(String profileId, Skill skill) {
        WorkerProfile profile = read(profileId);
        List<Skill> currentSkills = new java.util.ArrayList<>(profile.getSkills());

        if (!currentSkills.contains(skill)) {
            currentSkills.add(skill);
            return update(new WorkerProfile.Builder()
                    .copy(profile)
                    .setSkills(currentSkills)
                    .build());
        }

        return profile;
    }

    public WorkerProfile removeSkill(String profileId, Skill skill) {
        WorkerProfile profile = read(profileId);
        List<Skill> currentSkills = new java.util.ArrayList<>(profile.getSkills());

        if (currentSkills.contains(skill)) {
            currentSkills.remove(skill);
            if (currentSkills.isEmpty()) {
                throw new IllegalStateException("Worker must have at least one skill");
            }
            return update(new WorkerProfile.Builder()
                    .copy(profile)
                    .setSkills(currentSkills)
                    .build());
        }

        return profile;
    }

    public List<WorkerProfile> findAvailableProfiles() {
        return workerProfileRepository.findAvailableProfiles();
    }

    public List<WorkerProfile> findProfilesByLocation(String location) {
        return workerProfileRepository.findByLocation(location);
    }

    public List<WorkerProfile> findProfilesByHourlyRateRange(Double minRate, Double maxRate) {
        return workerProfileRepository.findByHourlyRateRange(minRate, maxRate);
    }

    public List<WorkerProfile> findProfilesByMinimumRating(Double minRating) {
        return workerProfileRepository.findByMinimumRating(minRating);
    }

    public List<WorkerProfile> findProfilesBySkill(Skill skill) {
        return workerProfileRepository.findBySkill(skill);
    }

    public List<WorkerProfile> findProfilesBySkillNames(List<String> skillNames) {
        return workerProfileRepository.findBySkillNames(skillNames);
    }

    public List<WorkerProfile> findVerifiedProfiles() {
        return workerProfileRepository.findVerifiedProfiles();
    }

    public List<WorkerProfile> findProfilesByVerificationStatus(WorkerProfile.VerificationStatus status) {
        return workerProfileRepository.findByVerificationStatus(status);
    }

    public List<WorkerProfile> findProfilesByAvailabilityStatus(WorkerProfile.AvailabilityStatus status) {
        return workerProfileRepository.findByAvailabilityStatus(status);
    }

    public List<WorkerProfile> searchProfiles(String location, List<String> skillNames,
                                              Double maxHourlyRate, Double minRating) {
        List<WorkerProfile> profiles = findVerifiedProfiles();

        return profiles.stream()
                .filter(p -> location == null || p.getLocation().equalsIgnoreCase(location))
                .filter(p -> maxHourlyRate == null || p.getHourlyRate() <= maxHourlyRate)
                .filter(p -> minRating == null || (p.getRating() != null && p.getRating() >= minRating))
                .filter(p -> skillNames == null || skillNames.isEmpty() ||
                        p.getSkills().stream().anyMatch(s -> skillNames.contains(s.getName())))
                .filter(p -> p.getAvailabilityStatus() == WorkerProfile.AvailabilityStatus.AVAILABLE)
                .toList();
    }

    public void updateProfileRatingFromReviews(String profileId) {
        WorkerProfile profile = read(profileId);
        Double averageRating = reviewService.getAverageRatingForUser(profile.getUser());

        if (averageRating != null && !averageRating.equals(profile.getRating())) {
            updateRating(profileId, averageRating);
        }
    }

    public boolean isProfileComplete(String profileId) {
        WorkerProfile profile = read(profileId);
        return profile.getBio() != null && !profile.getBio().trim().isEmpty() &&
                profile.getSkills() != null && !profile.getSkills().isEmpty() &&
                profile.getHourlyRate() != null &&
                profile.getLocation() != null && !profile.getLocation().trim().isEmpty();
    }

    public boolean canReceiveJobs(String profileId) {
        WorkerProfile profile = read(profileId);
        return profile.getVerificationStatus() == WorkerProfile.VerificationStatus.VERIFIED &&
                profile.getAvailabilityStatus() == WorkerProfile.AvailabilityStatus.AVAILABLE &&
                isProfileComplete(profileId);
    }

    public long countProfilesByStatus(WorkerProfile.VerificationStatus status) {
        return findProfilesByVerificationStatus(status).size();
    }

    public long countAvailableProfiles() {
        return findAvailableProfiles().size();
    }
}