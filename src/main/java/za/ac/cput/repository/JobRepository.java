package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Job;
import za.ac.cput.domain.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {

    // Find jobs by client
    List<Job> findByClient(User client);

    // Find jobs by status
    List<Job> findByStatus(Job.JobStatus status);

    // Find open jobs
    @Query("SELECT j FROM Job j WHERE j.status = 'OPEN'")
    List<Job> findOpenJobs();

    // Find jobs by category
    List<Job> findByCategory(String category);

    // Find jobs by location
    List<Job> findByLocation(String location);

    // Search jobs by title or description
    @Query("SELECT j FROM Job j WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Job> searchByKeyword(@Param("keyword") String keyword);

    // Find jobs by budget range
    @Query("SELECT j FROM Job j WHERE j.budget BETWEEN :minBudget AND :maxBudget")
    List<Job> findByBudgetRange(@Param("minBudget") Double minBudget,
                                @Param("maxBudget") Double maxBudget);

    // Find recent jobs (posted within last N days)
    @Query("SELECT j FROM Job j WHERE j.datePosted >= :date ORDER BY j.datePosted DESC")
    List<Job> findRecentJobs(@Param("date") LocalDateTime date);

    // Find jobs posted by client in a specific status
    @Query("SELECT j FROM Job j WHERE j.client = :client AND j.status = :status")
    List<Job> findByClientAndStatus(@Param("client") User client, @Param("status") Job.JobStatus status);

    // Find jobs by category and location
    List<Job> findByCategoryAndLocation(String category, String location);

    // Find jobs by status and location
    List<Job> findByStatusAndLocation(Job.JobStatus status, String location);

    // Count jobs by status
    Long countByStatus(Job.JobStatus status);

    // Find the highest paying jobs
    @Query("SELECT j FROM Job j WHERE j.status = 'OPEN' ORDER BY j.budget DESC")
    List<Job> findHighestPayingJobs();

    // Find jobs in specific categories
    @Query("SELECT j FROM Job j WHERE j.category IN :categories AND j.status = 'OPEN'")
    List<Job> findByCategoriesIn(@Param("categories") List<String> categories);
}