package za.ac.cput.factory;

import za.ac.cput.domain.*;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;

public class ReviewFactory {

    public static Review createReview(Contract contract, User reviewer, User reviewed,
                                      Integer rating, String comment) {

        // Validate inputs
        if (contract == null || reviewer == null || reviewed == null) {
            return null;
        }
        if (reviewer.getUserId().equals(reviewed.getUserId())) {
            return null; // Cannot review yourself
        }
        if (!ValidationHelper.isValidReviewRating(rating)) {
            return null;
        }
        if (comment != null && !ValidationHelper.isValidLength(comment, 10, 500)) {
            return null;
        }

        return new Review.Builder()
                .setReviewId(ValidationHelper.generateId())
                .setContract(contract)
                .setReviewer(reviewer)
                .setReviewed(reviewed)
                .setRating(rating)
                .setComment(comment != null ? comment.trim() : null)
                .setDatePosted(LocalDateTime.now())
                .build();
    }

    public static Review createQuickReview(Contract contract, User reviewer, User reviewed, Integer rating) {
        return createReview(contract, reviewer, reviewed, rating, null);
    }
}
