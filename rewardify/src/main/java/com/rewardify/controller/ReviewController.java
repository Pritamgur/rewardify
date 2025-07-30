package com.rewardify.controller;

import com.rewardify.dto.ReviewRequest;
import com.rewardify.dto.ReviewResponse;
import com.rewardify.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getReviewsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(new ReviewResponse(reviewService.getReviewsByProductId(productId)));
    }

    @PostMapping("/submit")
    public ResponseEntity<String> addComment(@RequestParam Long customerId,
                                             @RequestParam Long productId,
                                             @RequestBody ReviewRequest reviewRequest) {
        reviewService.submitReview(customerId, productId, reviewRequest);
        return ResponseEntity.ok("Comment added successfully");
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("Review deleted successfully");
    }

    @PostMapping("{reviewId}/helpful")
    public ResponseEntity<String> markReviewAsHelpful(@PathVariable Long reviewId) {
        reviewService.markReviewAsHelpful(reviewId);
        return ResponseEntity.ok("Review marked as helpful");
    }

    //Scheduler Based
    @PostMapping("/{reviewId}/evaluate")
    public ResponseEntity<String> evaluateReview(@PathVariable Long reviewId) {
        reviewService.evaluateReview(reviewId);
        return ResponseEntity.ok("Review evaluation updated");
    }

}
