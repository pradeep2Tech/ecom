package com.app.ecom.mono.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CartItem {
    private Long id;
    private Long userId;
    private Product product;
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
