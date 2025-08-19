package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Contract;
import za.ac.cput.domain.Job;
import za.ac.cput.domain.Review;
import za.ac.cput.domain.User;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ReviewFactoryTest {

    private Contract contract;
    private User client;
    private User worker;

    @BeforeEach
    void setUp() {
        client = UserFactory.createClientUser("Client", "User", "client@email.com", "Password123!");
        worker = UserFactory.createWorkerUser("Worker", "User", "worker@email.com", "Password123!", "0821234567");
        Job job = JobFactory.createJob(client, "Review Test Job",
                "Job for testing review functionality", "Testing", 4000.0, "Cape Town");
        contract = ContractFactory.createContract(job, client, worker,
                LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(1), 3500.0);
    }

    @Test
    void createReview() {
        String comment = "Excellent work! Delivered on time and exceeded expectations.";
        Review review = ReviewFactory.createReview(contract, client, worker, 5, comment);

        assertNotNull(review);
        assertEquals(contract, review.getContract());
        assertEquals(client, review.getReviewer());
        assertEquals(worker, review.getReviewed());
        assertEquals(5, review.getRating());
        assertEquals(comment, review.getComment());
        assertNotNull(review.getReviewId());
        assertNotNull(review.getDatePosted());
        System.out.println(review);
    }

    @Test
    void createReview_WorkerReviewingClient() {
        String comment = "Great client to work with, clear communication and prompt payments.";
        Review review = ReviewFactory.createReview(contract, worker, client, 4, comment);

        assertNotNull(review);
        assertEquals(worker, review.getReviewer());
        assertEquals(client, review.getReviewed());
        assertEquals(4, review.getRating());
        System.out.println(review);
    }

    @Test
    void createReview_WithoutComment() {
        Review review = ReviewFactory.createReview(contract, client, worker, 3, null);

        assertNotNull(review);
        assertEquals(3, review.getRating());
        assertNull(review.getComment());
    }

    @Test
    void createReview_InvalidContract() {
        Review review = ReviewFactory.createReview(null, client, worker, 5, "Great work!");
        assertNull(review);
    }

    @Test
    void createReview_InvalidReviewer() {
        Review review = ReviewFactory.createReview(contract, null, worker, 5, "Great work!");
        assertNull(review);
    }

    @Test
    void createReview_InvalidReviewed() {
        Review review = ReviewFactory.createReview(contract, client, null, 5, "Great work!");
        assertNull(review);
    }

    @Test
    void createReview_SelfReview() {
        Review review = ReviewFactory.createReview(contract, client, client, 5, "Self review");
        assertNull(review); // Should not allow self-reviews
    }

    @Test
    void createReview_InvalidRating() {
        // Test with null rating
        Review review = ReviewFactory.createReview(contract, client, worker, null, "Comment");
        assertNull(review);

        // Test with rating too low
        review = ReviewFactory.createReview(contract, client, worker, 0, "Comment");
        assertNull(review);

        // Test with rating too high
        review = ReviewFactory.createReview(contract, client, worker, 6, "Comment");
        assertNull(review);
    }

    @Test
    void createReview_InvalidComment() {
        // Test with comment too short
        Review review = ReviewFactory.createReview(contract, client, worker, 4, "Short");
        assertNull(review);

        // Test with comment too long
        review = ReviewFactory.createReview(contract, client, worker, 4, "A".repeat(501));
        assertNull(review);
    }

    @Test
    void createQuickReview() {
        Review review = ReviewFactory.createQuickReview(contract, client, worker, 4);

        assertNotNull(review);
        assertEquals(contract, review.getContract());
        assertEquals(client, review.getReviewer());
        assertEquals(worker, review.getReviewed());
        assertEquals(4, review.getRating());
        assertNull(review.getComment());
        System.out.println(review);
    }

    @Test
    void createQuickReview_InvalidInput() {
        Review review = ReviewFactory.createQuickReview(null, client, worker, 4);
        assertNull(review);
    }
}
