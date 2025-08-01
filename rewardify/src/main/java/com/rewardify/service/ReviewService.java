package com.rewardify.service;

import com.rewardify.dto.ReviewRequest;
import com.rewardify.entity.*;
import com.rewardify.exceeption.ResourceNotExistException;
import com.rewardify.exceeption.ReviewValidationException;
import com.rewardify.repository.ProductRepository;
import com.rewardify.repository.ReviewRepository;
import com.rewardify.repository.CustomerRepository;
import com.rewardify.repository.OrderRepository;
import com.rewardify.util.NLPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewValidatorService reviewValidatorService;

    @Autowired
    private NLPUtils nlpUtils;

    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId)
                .orElse(Collections.emptyList());
    }

    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotExistException("Review Not found"));
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
        product.ifPresentOrElse(e -> {
            review.setProduct(e);
            review.setVerified(true);
        }, () -> {
            productRepository.findById(productId)
                    .ifPresent(review::setProduct);
            review.setVerified(false);
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
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotExistException("Review not found"));
        try {
            reviewValidatorService.validateReview(review.getContent());
            review.setValid(true);
            System.out.println("Review is valid");
        } catch (ReviewValidationException exception) {
            System.out.println("Review validation failed: " + exception.getMessage());
            review.setValid(false);
        }
        Product product = productRepository.findById(review.getProduct().getId())
                .orElseThrow(() -> new ResourceNotExistException("Product not found"));
        String[] tokens = nlpUtils.getTokens(review.getContent());
        boolean isInFormative = isProductRelevant(tokens, product);
        System.out.println("Review informative: " + isInFormative);
        review.setInformative(isInFormative);

        reviewRepository.save(review);
    }

    public boolean isProductRelevant(String[] tokens, Product product) {
        Set<String> reviewTokens = Arrays.stream(tokens)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        long matches = Arrays.stream(product.getTags().split(","))
                .map(String::toLowerCase)
                .filter(reviewTokens::contains)
                .count();
        return matches >= 2; // adjust threshold based on your data
    }

}
