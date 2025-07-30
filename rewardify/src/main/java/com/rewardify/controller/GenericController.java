package com.rewardify.controller;

import com.rewardify.dto.CreateCustomerRequest;
import com.rewardify.dto.CreateOrderRequest;
import com.rewardify.entity.Customer;
import com.rewardify.entity.Order;
import com.rewardify.entity.Product;
import com.rewardify.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GenericController {

    @Autowired
    private GenericService genericService;

    @PostMapping("/api/v1/customers")
    public ResponseEntity<?> createCustomer(@RequestBody CreateCustomerRequest request) {
        // Logic to create a customer
        Customer customer = genericService.createCustomer(request);
        return ResponseEntity.ok(String.format("Customer with %d created successfully", customer.getId()));
    }

    @GetMapping("/api/v1/customers/{customerId}")
    public ResponseEntity<?> getCustomer(@PathVariable Long customerId) {
        // Get the customer via id
        return ResponseEntity.ok(genericService.getCustomer(customerId));
    }

    @PostMapping("/api/v1/orders")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        // Logic to create an order
        Order order = genericService.createOrder(request);
        return ResponseEntity.ok(String.format("Order with %d created successfully", order.getId()));
    }

    @GetMapping("/api/v1/orders/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        // Logic to get an order by id
        return ResponseEntity.ok("Order retrieved successfully");
    }

    @PostMapping("/api/v1/orders/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable Long orderId, @RequestParam String status) {
        // Logic to update an order
        Order updatedOrder = genericService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(String.format("Order with %d updated successfully", updatedOrder.getId()));
    }

    @PostMapping("/api/v1/products")
    public ResponseEntity<?> createProduct(@RequestBody Product request) {
        // Logic to create a product
        Product product = genericService.createProduct(request);
        return ResponseEntity.ok(String.format("Product with %d created successfully", product.getId()));
    }
}
