package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// ========================= REVIEW REPOSITORY =========================
@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

    // Find reviews by contract
    List<Review> findByContract(Contract contract);

    // Find reviews by reviewer
    List<Review> findByReviewer(User reviewer);

    // Find reviews by reviewed user
    List<Review> findByReviewed(User reviewed);

    // Find reviews by rating
    List<Review> findByRating(Integer rating);

    // Find reviews with rating greater than or equal to specified value
    @Query("SELECT r FROM Review r WHERE r.rating >= :minRating")
    List<Review> findByRatingGreaterThanEqual(@Param("minRating") Integer minRating);

    // Calculate average rating for a user
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewed = :user")
    Double getAverageRatingForUser(@Param("user") User user);

    // Find recent reviews
    @Query("SELECT r FROM Review r WHERE r.datePosted >= :date ORDER BY r.datePosted DESC")
    List<Review> findRecentReviews(@Param("date") LocalDateTime date);

    // Count reviews by rating
    Long countByRating(Integer rating);
}
