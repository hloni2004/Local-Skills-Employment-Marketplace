package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Application;
import za.ac.cput.domain.Job;
import za.ac.cput.domain.User;
import za.ac.cput.repository.ApplicationRepository;
import za.ac.cput.factory.ApplicationFactory;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ApplicationService implements IService<Application, String> {

    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository,
                              NotificationService notificationService) {
        this.applicationRepository = applicationRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Application create(Application application) {
        ValidationHelper.ValidationResult result = ValidationHelper.validateApplication(application);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid application data: " + result.getErrorMessage());
        }

        // Check if worker already applied for this job
        if (applicationRepository.existsByJobAndWorker(application.getJob(), application.getWorker())) {
            throw new IllegalStateException("Worker has already applied for this job");
        }

        Application savedApplication = applicationRepository.save(application);

        // Notify client of new application
        notificationService.createApplicationUpdate(
                application.getJob().getClient(),
                "New application received for job: " + application.getJob().getTitle()
        );

        return savedApplication;
    }

    @Override
    public Application read(String applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));
    }

    @Override
    public Application update(Application application) {
        if (!applicationRepository.existsById(application.getApplicationId())) {
            throw new RuntimeException("Application not found");
        }
        return applicationRepository.save(application);
    }

    @Override
    public Application delete(String applicationId) {
        Application application = read(applicationId);
        applicationRepository.deleteById(applicationId);
        return application;
    }

    // Business Logic Methods
    public Application applyForJob(Job job, User worker, String coverLetter, Double expectedPay) {
        Application application = ApplicationFactory.createApplication(job, worker, coverLetter, expectedPay);
        if (application == null) {
            throw new IllegalArgumentException("Invalid application data");
        }
        return create(application);
    }

    public Application acceptApplication(String applicationId) {
        Application application = read(applicationId);
        if (application.getStatus() != Application.ApplicationStatus.PENDING) {
            throw new IllegalStateException("Application is not in pending status");
        }

        Application updatedApplication = update(new Application.Builder()
                .copy(application)
                .setStatus(Application.ApplicationStatus.ACCEPTED)
                .build());

        // Notify worker of acceptance
        notificationService.createApplicationUpdate(
                application.getWorker(),
                "Your application for '" + application.getJob().getTitle() + "' has been accepted!"
        );

        // Reject other pending applications for this job
        rejectOtherApplications(application.getJob(), application.getApplicationId());

        return updatedApplication;
    }

    public Application rejectApplication(String applicationId, String reason) {
        Application application = read(applicationId);
        if (application.getStatus() != Application.ApplicationStatus.PENDING) {
            throw new IllegalStateException("Application is not in pending status");
        }

        Application updatedApplication = update(new Application.Builder()
                .copy(application)
                .setStatus(Application.ApplicationStatus.REJECTED)
                .build());

        // Notify worker of rejection
        notificationService.createApplicationUpdate(
                application.getWorker(),
                "Your application for '" + application.getJob().getTitle() + "' has been rejected."
        );

        return updatedApplication;
    }

    public Application withdrawApplication(String applicationId) {
        Application application = read(applicationId);
        if (application.getStatus() != Application.ApplicationStatus.PENDING) {
            throw new IllegalStateException("Application cannot be withdrawn from current status");
        }

        return update(new Application.Builder()
                .copy(application)
                .setStatus(Application.ApplicationStatus.WITHDRAWN)
                .build());
    }

    private void rejectOtherApplications(Job job, String acceptedApplicationId) {
        List<Application> pendingApplications = applicationRepository.findByJobAndStatus(
                job, Application.ApplicationStatus.PENDING
        );

        for (Application app : pendingApplications) {
            if (!app.getApplicationId().equals(acceptedApplicationId)) {
                update(new Application.Builder()
                        .copy(app)
                        .setStatus(Application.ApplicationStatus.REJECTED)
                        .build());

                // Notify workers of rejection
                notificationService.createApplicationUpdate(
                        app.getWorker(),
                        "Your application for '" + job.getTitle() + "' was not selected."
                );
            }
        }
    }

    public List<Application> findApplicationsByWorker(User worker) {
        return applicationRepository.findByWorker(worker);
    }

    public List<Application> findApplicationsByJob(Job job) {
        return applicationRepository.findByJob(job);
    }

    public List<Application> findApplicationsByJobClient(User client) {
        return applicationRepository.findByJobClient(client);
    }

    public List<Application> findPendingApplications() {
        return applicationRepository.findPendingApplications();
    }

    public List<Application> findRecentApplications(int days) {
        LocalDateTime date = LocalDateTime.now().minusDays(days);
        return applicationRepository.findRecentApplications(date);
    }

    public List<Application> findApplicationsByWorkerAndCategory(User worker, String category) {
        return applicationRepository.findByWorkerAndJobCategory(worker, category);
    }

    public Long countApplicationsByStatus(Application.ApplicationStatus status) {
        return applicationRepository.countByStatus(status);
    }
}
