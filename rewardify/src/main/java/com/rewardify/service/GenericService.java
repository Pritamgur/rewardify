package com.rewardify.service;

import com.rewardify.dto.CreateCustomerRequest;
import com.rewardify.dto.CreateOrderRequest;
import com.rewardify.entity.Customer;
import com.rewardify.entity.Order;
import com.rewardify.entity.Product;
import com.rewardify.repository.CustomerRepository;
import com.rewardify.repository.OrderRepository;
import com.rewardify.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenericService {
    //this is a service class to add a customer, retreive a customer, add a product, retrieve a product, add an order, retrieve an order
    //and to retrieve all orders, products and customers. It will also contain methods to update and delete these entities.

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    //Add a method to create a customer
    public Customer createCustomer(CreateCustomerRequest request) {

        customerRepository.findByEmail(request.getEmail()).ifPresent(customer -> {
            throw new IllegalArgumentException("Customer with this email already exists");
        });
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        return customerRepository.save(customer);
    }

    //Get a customer by ID
    public Customer getCustomer(Long customerId) {
        // Logic to get a customer by ID
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    // Add methods for creating, retrieving, updating, and deleting products and orders
    //Add a method to create an order
    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomer(getCustomer(request.getCustomerId()));
        order.setProducts(findAllProducts(request.getProducts()));
        return orderRepository.save(order);
    }

    //Update order status
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setCompleted("completed".equalsIgnoreCase(status));
        return orderRepository.save(order);
    }

    //add a product
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAllProducts(List<Long> products) {
        return productRepository.findAllById(products);
    }

}
