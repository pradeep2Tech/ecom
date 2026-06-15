package com.app.ecom.mono.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private String imageUrl;
    private boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
