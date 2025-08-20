package za.ac.cput.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import za.ac.cput.domain.User;
import za.ac.cput.domain.*;

import java.util.*;
import java.util.Optional;

@Repository
public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, String> {

    // Find profile by user
    Optional<WorkerProfile> findByUser(User user);

    // Find profiles by availability status
    List<WorkerProfile> findByAvailabilityStatus(WorkerProfile.AvailabilityStatus status);

    // Find available profiles
    @Query("SELECT wp FROM WorkerProfile wp WHERE wp.availabilityStatus = 'AVAILABLE'")
    List<WorkerProfile> findAvailableProfiles();

    // Find profiles by location
    List<WorkerProfile> findByLocation(String location);

    // Find profiles by hourly rate range
    @Query("SELECT wp FROM WorkerProfile wp WHERE wp.hourlyRate BETWEEN :minRate AND :maxRate")
    List<WorkerProfile> findByHourlyRateRange(@Param("minRate") Double minRate, @Param("maxRate") Double maxRate);

    // Find profiles by rating range
    @Query("SELECT wp FROM WorkerProfile wp WHERE wp.rating >= :minRating")
    List<WorkerProfile> findByMinimumRating(@Param("minRating") Double minRating);

    // Find profiles with specific skill
    @Query("SELECT wp FROM WorkerProfile wp JOIN wp.skills s WHERE s = :skill")
    List<WorkerProfile> findBySkill(@Param("skill") Skill skill);

    // Find profiles by verification status
    List<WorkerProfile> findByVerificationStatus(WorkerProfile.VerificationStatus status);

    // Find verified profiles
    @Query("SELECT wp FROM WorkerProfile wp WHERE wp.verificationStatus = 'VERIFIED'")
    List<WorkerProfile> findVerifiedProfiles();

    // Search profiles by skills
    @Query("SELECT DISTINCT wp FROM WorkerProfile wp JOIN wp.skills s WHERE s.name IN :skillNames")
    List<WorkerProfile> findBySkillNames(@Param("skillNames") List<String> skillNames);
}