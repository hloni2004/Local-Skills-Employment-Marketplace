package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Application;
import za.ac.cput.domain.Job;
import za.ac.cput.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {

    // Find applications by worker
    List<Application> findByWorker(User worker);

    // Find applications by job
    List<Application> findByJob(Job job);

    // Find applications by status
    List<Application> findByStatus(Application.ApplicationStatus status);

    // Find application by job and worker (to check if worker already applied)
    Optional<Application> findByJobAndWorker(Job job, User worker);

    // Find applications for jobs posted by a specific client
    @Query("SELECT a FROM Application a WHERE a.job.client = :client")
    List<Application> findByJobClient(@Param("client") User client);

    // Find pending applications
    @Query("SELECT a FROM Application a WHERE a.status = 'PENDING'")
    List<Application> findPendingApplications();

    // Find applications by worker and status
    List<Application> findByWorkerAndStatus(User worker, Application.ApplicationStatus status);

    // Find applications by job and status
    List<Application> findByJobAndStatus(Job job, Application.ApplicationStatus status);

    // Find recent applications (applied within last N days)
    @Query("SELECT a FROM Application a WHERE a.dateApplied >= :date ORDER BY a.dateApplied DESC")
    List<Application> findRecentApplications(@Param("date") LocalDateTime date);

    // Find applications with expected pay within range
    @Query("SELECT a FROM Application a WHERE a.expectedPay BETWEEN :minPay AND :maxPay")
    List<Application> findByExpectedPayRange(@Param("minPay") Double minPay, @Param("maxPay") Double maxPay);

    // Count applications by status
    Long countByStatus(Application.ApplicationStatus status);

    // Find worker's applications for a specific job category
    @Query("SELECT a FROM Application a WHERE a.worker = :worker AND a.job.category = :category")
    List<Application> findByWorkerAndJobCategory(@Param("worker") User worker, @Param("category") String category);

    // Find applications for jobs in specific location
    @Query("SELECT a FROM Application a WHERE a.job.location = :location")
    List<Application> findByJobLocation(@Param("location") String location);

    // Check if application exists for job and worker combination
    @Query("SELECT COUNT(a) > 0 FROM Application a WHERE a.job = :job AND a.worker = :worker")
    boolean existsByJobAndWorker(@Param("job") Job job, @Param("worker") User worker);

    // Find applications ordered by expected pay
    @Query("SELECT a FROM Application a WHERE a.job = :job ORDER BY a.expectedPay ASC")
    List<Application> findByJobOrderByExpectedPayAsc(@Param("job") Job job);
}