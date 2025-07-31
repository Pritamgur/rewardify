package com.rewardify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private Long customerId;
    private Set<Long> orderItems;
}
