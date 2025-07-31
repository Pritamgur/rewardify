package com.rewardify.service;

import com.rewardify.dto.ReviewRequest;
import com.rewardify.entity.*;
import com.rewardify.exceeption.ResourceNotExistException;
import com.rewardify.repository.ReviewRepository;
import com.rewardify.repository.CustomerRepository;
import com.rewardify.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId)
                .orElse(Collections.emptyList());
    }

    public void submitReview(Long customerId, Long productId, ReviewRequest reviewRequest) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotExistException("Customer not found"));

        List<Order> orders = orderRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new ResourceNotExistException("Customer has no orders"));

        Optional<Product> product = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(OrderItems::getProduct)
                .filter(e -> e.getId().equals(productId))
                .findAny();

        Review review = new Review();
        review.setCustomer(customer);
        product.ifPresent(e -> {
            review.setProduct(e);
            review.setVerified(true);
        });
        review.setContent(reviewRequest.getReviewText());
        //To set the review as helpful or not
        //To validate if the
        //review.setHelpful(reviewRequest.isHelpful());
        review.setRating(reviewRequest.getRating());
        reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.findById(reviewId)
                .ifPresentOrElse(reviewRepository::delete,
                                 () -> { throw new ResourceNotExistException("Review not found"); });
    }

    public void markReviewAsHelpful(Long reviewId) {
        reviewRepository.findById(reviewId)
                .ifPresentOrElse(review -> {
                    review.setHelpful(true);
                    review.setHelpfulVotesCount(review.getHelpfulVotesCount() + 1);
                    reviewRepository.save(review);
                }, () -> {
                    throw new ResourceNotExistException("Review not found");
                });
    }

    public void evaluateReview(Long reviewId) {
    }
}