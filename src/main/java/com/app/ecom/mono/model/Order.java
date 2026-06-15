package com.app.ecom.mono.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class Order {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderItem> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
