package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Contract;
import za.ac.cput.domain.Review;
import za.ac.cput.domain.User;
import za.ac.cput.repository.ReviewRepository;
import za.ac.cput.factory.ReviewFactory;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReviewService implements IService<Review, String> {

    private final ReviewRepository reviewRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         NotificationService notificationService,
                         UserService userService) {
        this.reviewRepository = reviewRepository;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @Override
    public Review create(Review review) {
        ValidationHelper.ValidationResult result = ValidationHelper.validateReview(review);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid review data: " + result.getErrorMessage());
        }

        Review savedReview = reviewRepository.save(review);

        // Notify the reviewed user
        notificationService.createSystemNotification(
                review.getReviewed(),
                "You received a " + review.getRating() + "-star review from " +
                        review.getReviewer().getFirstName()
        );

        // Update user's average rating if they're a worker
        updateWorkerRating(review.getReviewed());

        return savedReview;
    }

    @Override
    public Review read(String reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + reviewId));
    }

    @Override
    public Review update(Review review) {
        if (!reviewRepository.existsById(review.getReviewId())) {
            throw new RuntimeException("Review not found");
        }

        ValidationHelper.ValidationResult result = ValidationHelper.validateReview(review);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid review data: " + result.getErrorMessage());
        }

        Review updatedReview = reviewRepository.save(review);

        // Update user's average rating after review update
        updateWorkerRating(review.getReviewed());

        return updatedReview;
    }

    @Override
    public Review delete(String reviewId) {
        Review review = read(reviewId);
        reviewRepository.deleteById(reviewId);

        // Update user's average rating after review deletion
        updateWorkerRating(review.getReviewed());

        return review;
    }

    // Business Logic Methods
    public Review createReview(Contract contract, User reviewer, User reviewed,
                               Integer rating, String comment) {

        // Validate contract is completed
        if (contract.getStatus() != Contract.ContractStatus.COMPLETED) {
            throw new IllegalStateException("Can only review completed contracts");
        }

        // Validate reviewer is part of the contract
        if (!contract.getClient().equals(reviewer) && !contract.getWorker().equals(reviewer)) {
            throw new IllegalArgumentException("Reviewer is not part of this contract");
        }

        // Validate reviewed user is the other party in the contract
        User expectedReviewed = contract.getClient().equals(reviewer) ?
                contract.getWorker() : contract.getClient();
        if (!expectedReviewed.equals(reviewed)) {
            throw new IllegalArgumentException("Invalid reviewed user for this contract");
        }

        Review review = ReviewFactory.createReview(contract, reviewer, reviewed, rating, comment);
        if (review == null) {
            throw new IllegalArgumentException("Invalid review creation data");
        }

        return create(review);
    }

    public Review createQuickReview(Contract contract, User reviewer, User reviewed, Integer rating) {
        return createReview(contract, reviewer, reviewed, rating, null);
    }

    public List<Review> findReviewsByContract(Contract contract) {
        return reviewRepository.findByContract(contract);
    }

    public List<Review> findReviewsByReviewer(User reviewer) {
        return reviewRepository.findByReviewer(reviewer);
    }

    public List<Review> findReviewsByReviewed(User reviewed) {
        return reviewRepository.findByReviewed(reviewed);
    }

    public List<Review> findReviewsByRating(Integer rating) {
        return reviewRepository.findByRating(rating);
    }

    public List<Review> findReviewsWithMinimumRating(Integer minRating) {
        return reviewRepository.findByRatingGreaterThanEqual(minRating);
    }

    public List<Review> findRecentReviews(int days) {
        LocalDateTime date = LocalDateTime.now().minusDays(days);
        return reviewRepository.findRecentReviews(date);
    }

    public Double getAverageRatingForUser(User user) {
        Double averageRating = reviewRepository.getAverageRatingForUser(user);
        return averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : null;
    }

    public Long countReviewsByRating(Integer rating) {
        return reviewRepository.countByRating(rating);
    }

    public boolean hasReviewed(Contract contract, User reviewer) {
        List<Review> reviews = reviewRepository.findByContract(contract);
        return reviews.stream().anyMatch(review -> review.getReviewer().equals(reviewer));
    }

    public boolean canReview(Contract contract, User user) {
        // Can only review completed contracts
        if (contract.getStatus() != Contract.ContractStatus.COMPLETED) {
            return false;
        }

        // User must be part of the contract
        if (!contract.getClient().equals(user) && !contract.getWorker().equals(user)) {
            return false;
        }

        // User hasn't already reviewed this contract
        return !hasReviewed(contract, user);
    }

    private void updateWorkerRating(User user) {
        // Update worker profile rating if user has a worker profile
        if (user.getWorkerProfile() != null) {
            Double averageRating = getAverageRatingForUser(user);

            if (averageRating != null) {
                // This would require WorkerProfileService to update the rating
                // For now, we'll just notify - in a complete implementation,
                // we'd inject WorkerProfileService here
                System.out.println("Worker rating updated for user: " + user.getUserId() +
                        " New rating: " + averageRating);
            }
        }
    }

    public List<Review> getTopRatedUserReviews(User user, int limit) {
        return reviewRepository.findByReviewed(user).stream()
                .sorted((r1, r2) -> r2.getRating().compareTo(r1.getRating()))
                .limit(limit)
                .toList();
    }

    public boolean hasGoodRating(User user, double minimumRating) {
        Double averageRating = getAverageRatingForUser(user);
        return averageRating != null && averageRating >= minimumRating;
    }
}