package com.rewardify.service;

import com.rewardify.entity.Customer;
import com.rewardify.entity.Review;
import com.rewardify.exceeption.ResourceNotExistException;
import com.rewardify.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RewardService {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CustomerRepository customerRepository;

    public String rewardForHelpfulReview(Long reviewId) {
        // Fetch the review by ID
        String message;
        Review review = reviewService.getReviewById(reviewId);
        if (review == null) {
            throw new ResourceNotExistException("Review not found with ID: " + reviewId);
        }
        // Check if the review is valid and marked as helpful
        if (review.isValid() && review.isHelpful() && review.isInformative() && review.isVerified()) {
            Customer customer = review.getCustomer();
            if (customer.getRewardPoints() > 0) {
                return "Customer " + customer.getName() + " already rewarded.";
            }
            int rewardPoints = calculateRewardPoints(review);
            message = "Customer " + customer.getName() + " rewarded with "
                    + rewardPoints + " points for review ID: " + reviewId;
            customer.setRewardPoints(customer.getRewardPoints() + rewardPoints);
            customerRepository.save(customer);
        } else {
            message = "Review ID: " + reviewId + " is not eligible for rewards.";
        }
        return message;
    }

    private int calculateRewardPoints(Review review) {
        return review.getHelpfulVotesCount() * 10;
    }
}
