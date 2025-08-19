package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
public class Review {
    @Id
    @Column(name = "review_id")
    protected String reviewId;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    protected Contract contract;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    protected User reviewer;

    @ManyToOne
    @JoinColumn(name = "reviewed_id", nullable = false)
    protected User reviewed;

    @Column(name = "rating")
    protected Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    protected String comment;

    @Column(name = "date_posted")
    protected LocalDateTime datePosted;

    protected Review() {}

    public Review(Builder builder) {
        this.reviewId = builder.reviewId;
        this.contract = builder.contract;
        this.reviewer = builder.reviewer;
        this.reviewed = builder.reviewed;
        this.rating = builder.rating;
        this.comment = builder.comment;
        this.datePosted = builder.datePosted;
    }

    // Getters
    public String getReviewId() { return reviewId; }
    public Contract getContract() { return contract; }
    public User getReviewer() { return reviewer; }
    public User getReviewed() { return reviewed; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getDatePosted() { return datePosted; }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId='" + reviewId + '\'' +
                ", contract=" + contract.getContractId() +
                ", reviewer=" + reviewer.getUserId() +
                ", reviewed=" + reviewed.getUserId() +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", datePosted=" + datePosted +
                '}';
    }

    public static class Builder {
        private String reviewId;
        private Contract contract;
        private User reviewer;
        private User reviewed;
        private Integer rating;
        private String comment;
        private LocalDateTime datePosted;

        public Builder setReviewId(String reviewId) { this.reviewId = reviewId; return this; }
        public Builder setContract(Contract contract) { this.contract = contract; return this; }
        public Builder setReviewer(User reviewer) { this.reviewer = reviewer; return this; }
        public Builder setReviewed(User reviewed) { this.reviewed = reviewed; return this; }
        public Builder setRating(Integer rating) { this.rating = rating; return this; }
        public Builder setComment(String comment) { this.comment = comment; return this; }
        public Builder setDatePosted(LocalDateTime datePosted) { this.datePosted = datePosted; return this; }

        public Builder copy(Review review) {
            this.reviewId = review.reviewId;
            this.contract = review.contract;
            this.reviewer = review.reviewer;
            this.reviewed = review.reviewed;
            this.rating = review.rating;
            this.comment = review.comment;
            this.datePosted = review.datePosted;
            return this;
        }

        public Review build() { return new Review(this); }
    }
}