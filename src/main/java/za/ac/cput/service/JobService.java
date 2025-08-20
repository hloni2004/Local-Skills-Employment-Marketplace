package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Job;
import za.ac.cput.domain.User;
import za.ac.cput.repository.JobRepository;
import za.ac.cput.factory.JobFactory;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class JobService implements IService<Job, String> {

    private final JobRepository jobRepository;

    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public Job create(Job job) {
        ValidationHelper.ValidationResult result = ValidationHelper.validateJob(job);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid job data: " + result.getErrorMessage());
        }
        return jobRepository.save(job);
    }

    @Override
    public Job read(String jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
    }

    @Override
    public Job update(Job job) {
        if (!jobRepository.existsById(job.getJobId())) {
            throw new RuntimeException("Job not found");
        }

        ValidationHelper.ValidationResult result = ValidationHelper.validateJob(job);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid job data: " + result.getErrorMessage());
        }

        return jobRepository.save(job);
    }

    @Override
    public Job delete(String jobId) {
        Job job = read(jobId);
        jobRepository.deleteById(jobId);
        return job;
    }

    // Business Logic Methods
    public Job postJob(User client, String title, String description, String category,
                       Double budget, String location) {
        Job newJob = JobFactory.createJob(client, title, description, category, budget, location);
        if (newJob == null) {
            throw new IllegalArgumentException("Invalid job creation data");
        }
        return create(newJob);
    }

    public Job assignJob(String jobId, User worker) {
        Job job = read(jobId);
        if (job.getStatus() != Job.JobStatus.OPEN) {
            throw new IllegalStateException("Job is not available for assignment");
        }

        return update(new Job.Builder().copy(job).setStatus(Job.JobStatus.ASSIGNED).build());
    }

    public Job completeJob(String jobId) {
        Job job = read(jobId);
        if (job.getStatus() != Job.JobStatus.ASSIGNED) {
            throw new IllegalStateException("Job cannot be completed from current status");
        }

        return update(new Job.Builder().copy(job).setStatus(Job.JobStatus.COMPLETED).build());
    }

    public Job cancelJob(String jobId) {
        Job job = read(jobId);
        if (job.getStatus() == Job.JobStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed job");
        }

        return update(new Job.Builder().copy(job).setStatus(Job.JobStatus.CANCELLED).build());
    }

    public List<Job> findOpenJobs() {
        return jobRepository.findOpenJobs();
    }

    public List<Job> findJobsByClient(User client) {
        return jobRepository.findByClient(client);
    }

    public List<Job> findJobsByCategory(String category) {
        return jobRepository.findByCategory(category);
    }

    public List<Job> findJobsByLocation(String location) {
        return jobRepository.findByLocation(location);
    }

    public List<Job> searchJobs(String keyword) {
        return jobRepository.searchByKeyword(keyword);
    }

    public List<Job> findJobsByBudgetRange(Double minBudget, Double maxBudget) {
        return jobRepository.findByBudgetRange(minBudget, maxBudget);
    }

    public List<Job> findRecentJobs(int days) {
        LocalDateTime date = LocalDateTime.now().minusDays(days);
        return jobRepository.findRecentJobs(date);
    }

    public List<Job> findHighestPayingJobs() {
        return jobRepository.findHighestPayingJobs();
    }

    public List<Job> findJobsByCategories(List<String> categories) {
        return jobRepository.findByCategoriesIn(categories);
    }

    public Long countJobsByStatus(Job.JobStatus status) {
        return jobRepository.countByStatus(status);
    }
}