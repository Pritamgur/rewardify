package com.rewardify.repository;

import com.rewardify.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<List<Order>> findByCustomerId(Long id);

    // Custom query methods can be defined here if needed
    // For example, to find orders by customer ID or status
    // List<Order> findByCustomerId(Long customerId);
    // List<Order> findByCompleted(boolean completed);
}
