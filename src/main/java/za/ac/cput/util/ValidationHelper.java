package za.ac.cput.util;

import org.apache.commons.validator.routines.EmailValidator;
import za.ac.cput.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;


public class ValidationHelper {

    // Constants for validation
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+27|0)[6-8][0-9]{8}$"); // South African phone numbers
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]{2,50}$");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9]{4}$"); // South African postal codes
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;
    private static final double MIN_HOURLY_RATE = 50.0; // Minimum R50 per hour
    private static final double MAX_HOURLY_RATE = 10000.0; // Maximum R10,000 per hour
    private static final int MIN_BIO_LENGTH = 10;
    private static final int MAX_BIO_LENGTH = 1000;

    // ID Generation
    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    // Basic validation methods
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (isNullOrEmpty(value)) return false;
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }

    // User validation methods
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) return false;
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean isValidPassword(String password) {
        if (isNullOrEmpty(password)) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidName(String name) {
        if (isNullOrEmpty(name)) return false;
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (isNullOrEmpty(phoneNumber)) return false;
        return PHONE_PATTERN.matcher(phoneNumber.trim()).matches();
    }

    public static boolean isValidPostalCode(String postalCode) {
        if (isNullOrEmpty(postalCode)) return false;
        return POSTAL_CODE_PATTERN.matcher(postalCode.trim()).matches();
    }

    public static boolean isValidUserId(String userId) {
        if (isNullOrEmpty(userId)) return false;
        try {
            UUID.fromString(userId);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // User-specific validation
    public static ValidationResult validateUser(User user) {
        ValidationResult result = new ValidationResult();

        if (user == null) {
            result.addError("User cannot be null");
            return result;
        }

        if (!isValidUserId(user.getUserId())) {
            result.addError("Invalid user ID format");
        }

        if (!isValidName(user.getFirstName())) {
            result.addError("First name must be 2-50 characters and contain only letters and spaces");
        }

        if (!isValidName(user.getLastName())) {
            result.addError("Last name must be 2-50 characters and contain only letters and spaces");
        }

        if (!isValidEmail(user.getEmail())) {
            result.addError("Invalid email format");
        }

        if (!isValidPassword(user.getPassword())) {
            result.addError("Password must be at least 8 characters with uppercase, lowercase, number, and special character");
        }

        if (user.getPhoneNumber() != null && !isValidPhoneNumber(user.getPhoneNumber())) {
            result.addError("Invalid South African phone number format");
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            result.addError("User must have at least one role");
        }

        if (user.getStatus() == null) {
            result.addError("User status is required");
        }

        if (user.getDateJoined() == null) {
            result.addError("Date joined is required");
        } else if (user.getDateJoined().isAfter(LocalDateTime.now())) {
            result.addError("Date joined cannot be in the future");
        }

        return result;
    }

    // Job validation methods
    public static boolean isValidBudget(Double budget) {
        return budget != null && budget > 0 && budget <= 1000000; // Max R1M budget
    }

    public static ValidationResult validateJob(Job job) {
        ValidationResult result = new ValidationResult();

        if (job == null) {
            result.addError("Job cannot be null");
            return result;
        }

        if (isNullOrEmpty(job.getJobId()) || !isValidUserId(job.getJobId())) {
            result.addError("Invalid job ID format");
        }

        if (job.getClient() == null) {
            result.addError("Job must have a client");
        }

        if (!isValidLength(job.getTitle(), 5, 100)) {
            result.addError("Job title must be 5-100 characters long");
        }

        if (!isValidLength(job.getDescription(), 20, 2000)) {
            result.addError("Job description must be 20-2000 characters long");
        }

        if (isNullOrEmpty(job.getCategory())) {
            result.addError("Job category is required");
        }

        if (!isValidBudget(job.getBudget())) {
            result.addError("Budget must be positive and not exceed R1,000,000");
        }

        if (isNullOrEmpty(job.getLocation())) {
            result.addError("Job location is required");
        }

        if (job.getDatePosted() == null) {
            result.addError("Date posted is required");
        }

        if (job.getStatus() == null) {
            result.addError("Job status is required");
        }

        return result;
    }

    // WorkerProfile validation methods
    public static boolean isValidHourlyRate(Double hourlyRate) {
        return hourlyRate != null && hourlyRate >= MIN_HOURLY_RATE && hourlyRate <= MAX_HOURLY_RATE;
    }

    public static boolean isValidRating(Double rating) {
        return rating != null && rating >= MIN_RATING && rating <= MAX_RATING;
    }

    public static ValidationResult validateWorkerProfile(WorkerProfile profile) {
        ValidationResult result = new ValidationResult();

        if (profile == null) {
            result.addError("Worker profile cannot be null");
            return result;
        }

        if (isNullOrEmpty(profile.getProfileId()) || !isValidUserId(profile.getProfileId())) {
            result.addError("Invalid profile ID format");
        }

        if (profile.getUser() == null) {
            result.addError("Worker profile must be associated with a user");
        }

        if (profile.getBio() != null && !isValidLength(profile.getBio(), MIN_BIO_LENGTH, MAX_BIO_LENGTH)) {
            result.addError("Bio must be 10-1000 characters long");
        }

        if (profile.getSkills() == null || profile.getSkills().isEmpty()) {
            result.addError("Worker must have at least one skill");
        }

        if (!isValidHourlyRate(profile.getHourlyRate())) {
            result.addError("Hourly rate must be between R" + MIN_HOURLY_RATE + " and R" + MAX_HOURLY_RATE);
        }

        if (profile.getAvailabilityStatus() == null) {
            result.addError("Availability status is required");
        }

        if (isNullOrEmpty(profile.getLocation())) {
            result.addError("Worker location is required");
        }

        if (profile.getRating() != null && !isValidRating(profile.getRating())) {
            result.addError("Rating must be between " + MIN_RATING + " and " + MAX_RATING);
        }

        return result;
    }

    // Application validation methods
    public static ValidationResult validateApplication(Application application) {
        ValidationResult result = new ValidationResult();

        if (application == null) {
            result.addError("Application cannot be null");
            return result;
        }

        if (isNullOrEmpty(application.getApplicationId()) || !isValidUserId(application.getApplicationId())) {
            result.addError("Invalid application ID format");
        }

        if (application.getJob() == null) {
            result.addError("Application must be associated with a job");
        }

        if (application.getWorker() == null) {
            result.addError("Application must be associated with a worker");
        }

        if (application.getCoverLetter() != null && !isValidLength(application.getCoverLetter(), 20, 1000)) {
            result.addError("Cover letter must be 20-1000 characters long");
        }

        if (application.getExpectedPay() != null && !isValidBudget(application.getExpectedPay())) {
            result.addError("Expected pay must be positive and reasonable");
        }

        if (application.getStatus() == null) {
            result.addError("Application status is required");
        }

        if (application.getDateApplied() == null) {
            result.addError("Date applied is required");
        }

        return result;
    }

    // Review validation methods
    public static boolean isValidReviewRating(Integer rating) {
        return rating != null && rating >= MIN_RATING && rating <= MAX_RATING;
    }

    public static ValidationResult validateReview(Review review) {
        ValidationResult result = new ValidationResult();

        if (review == null) {
            result.addError("Review cannot be null");
            return result;
        }

        if (isNullOrEmpty(review.getReviewId()) || !isValidUserId(review.getReviewId())) {
            result.addError("Invalid review ID format");
        }

        if (review.getContract() == null) {
            result.addError("Review must be associated with a contract");
        }

        if (review.getReviewer() == null) {
            result.addError("Review must have a reviewer");
        }

        if (review.getReviewed() == null) {
            result.addError("Review must have a reviewed user");
        }

        if (review.getReviewer() != null && review.getReviewed() != null &&
                review.getReviewer().getUserId().equals(review.getReviewed().getUserId())) {
            result.addError("User cannot review themselves");
        }

        if (!isValidReviewRating(review.getRating())) {
            result.addError("Rating must be between " + MIN_RATING + " and " + MAX_RATING);
        }

        if (review.getComment() != null && !isValidLength(review.getComment(), 10, 500)) {
            result.addError("Review comment must be 10-500 characters long");
        }

        if (review.getDatePosted() == null) {
            result.addError("Date posted is required");
        }

        return result;
    }

    // Contract validation methods
    public static ValidationResult validateContract(Contract contract) {
        ValidationResult result = new ValidationResult();

        if (contract == null) {
            result.addError("Contract cannot be null");
            return result;
        }

        if (isNullOrEmpty(contract.getContractId()) || !isValidUserId(contract.getContractId())) {
            result.addError("Invalid contract ID format");
        }

        if (contract.getJob() == null) {
            result.addError("Contract must be associated with a job");
        }

        if (contract.getClient() == null) {
            result.addError("Contract must have a client");
        }

        if (contract.getWorker() == null) {
            result.addError("Contract must have a worker");
        }

        if (contract.getStartDate() == null) {
            result.addError("Start date is required");
        }

        if (contract.getEndDate() != null && contract.getStartDate() != null &&
                contract.getEndDate().isBefore(contract.getStartDate())) {
            result.addError("End date cannot be before start date");
        }

        if (!isValidBudget(contract.getAgreedPay())) {
            result.addError("Agreed pay must be positive and reasonable");
        }

        if (contract.getStatus() == null) {
            result.addError("Contract status is required");
        }

        return result;
    }

    // Skill validation methods
    public static ValidationResult validateSkill(Skill skill) {
        ValidationResult result = new ValidationResult();

        if (skill == null) {
            result.addError("Skill cannot be null");
            return result;
        }

        if (isNullOrEmpty(skill.getSkillId()) || !isValidUserId(skill.getSkillId())) {
            result.addError("Invalid skill ID format");
        }

        if (!isValidLength(skill.getName(), 2, 50)) {
            result.addError("Skill name must be 2-50 characters long");
        }

        if (isNullOrEmpty(skill.getCategory())) {
            result.addError("Skill category is required");
        }

        if (skill.getDescription() != null && !isValidLength(skill.getDescription(), 10, 500)) {
            result.addError("Skill description must be 10-500 characters long");
        }

        if (skill.getVerificationStatus() == null) {
            result.addError("Verification status is required");
        }

        return result;
    }

    // Business logic validation
    public static ValidationResult validateBusinessRules(Object entity) {
        ValidationResult result = new ValidationResult();

        // Add specific business rule validations based on entity type
        if (entity instanceof User) {
            User user = (User) entity;

            // Business rule: If user has WORKER role, they should have a worker profile
            if (user.getRoles() != null &&
                    (user.getRoles().contains(User.Role.WORKER) || user.getRoles().contains(User.Role.BOTH)) &&
                    user.getWorkerProfile() == null) {
                result.addWarning("Workers should have a complete profile");
            }
        }

        return result;
    }

    // Validation Result class to hold validation results
    public static class ValidationResult {
        private final java.util.List<String> errors;
        private final java.util.List<String> warnings;

        public ValidationResult() {
            this.errors = new java.util.ArrayList<>();
            this.warnings = new java.util.ArrayList<>();
        }

        public void addError(String error) {
            errors.add(error);
        }

        public void addWarning(String warning) {
            warnings.add(warning);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }

        public List<String> getErrors() {
            return java.util.Collections.unmodifiableList(errors);
        }

        public List<String> getWarnings() {
            return java.util.Collections.unmodifiableList(warnings);
        }

        public String getErrorMessage() {
            return String.join("; ", errors);
        }

        public String getWarningMessage() {
            return String.join("; ", warnings);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (!errors.isEmpty()) {
                sb.append("Errors: ").append(getErrorMessage());
            }
            if (!warnings.isEmpty()) {
                if (sb.length() > 0) sb.append(" | ");
                sb.append("Warnings: ").append(getWarningMessage());
            }
            return sb.toString();
        }
    }
}