package com.app.ecom.mono.service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.app.ecom.mono.Repo.ICartItemRepo;
import com.app.ecom.mono.Repo.IOrderItemRepo;
import com.app.ecom.mono.Repo.IOrderRepo;
import com.app.ecom.mono.Repo.IUserRepo;
import com.app.ecom.mono.entity.CartItemEntity;
import com.app.ecom.mono.entity.OrderEntity;
import com.app.ecom.mono.entity.OrderItemEntity;
import com.app.ecom.mono.entity.UserEntity;
import com.app.ecom.mono.model.Order;
import com.app.ecom.mono.model.OrderItem;
import com.app.ecom.mono.model.OrderStatus;

@Service
public class OrderService implements IOrderService {

    private final IOrderRepo orderRepo;
    private final IOrderItemRepo orderItemRepo;
    private final IUserRepo userRepo;
    private final ICartItemRepo cartItemRepo;

    public OrderService(IOrderRepo orderRepo, IOrderItemRepo orderItemRepo, IUserRepo userRepo, ICartItemRepo cartItemRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.userRepo = userRepo;
        this.cartItemRepo = cartItemRepo;
    }

    @Override
    public Order createOrder(Long userId) {
        UserEntity user = userRepo.findById(userId).orElseThrow(() -> new NoSuchElementException("user not found"));

        java.util.List<CartItemEntity> cartItems = cartItemRepo.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("cart is empty");
        }

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        java.util.List<OrderItemEntity> orderItems = new java.util.ArrayList<>();

        for (CartItemEntity cartItem : cartItems) {
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(cartItem.getPrice());
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        OrderEntity saved = orderRepo.save(order);

        cartItemRepo.deleteAll(cartItems);

        return toModel(saved);
    }

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepo.findById(orderId).map(this::toModel);
    }

    @Override
    public java.util.List<Order> getOrdersByUserId(Long userId) {
        userRepo.findById(userId).orElseThrow(() -> new NoSuchElementException("user not found"));
        return orderRepo.findAll().stream()
                .filter(o -> o.getUser() != null && o.getUser().getId().equals(userId))
                .map(this::toModel)
                .toList();
    }

    private Order toModel(OrderEntity entity) {
        Order model = new Order();
        model.setId(entity.getId());
        model.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        model.setTotalAmount(entity.getTotalAmount());
        model.setStatus(entity.getStatus());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getOrderItems() != null) {
            model.setOrderItems(entity.getOrderItems().stream().map(this::toOrderItemModel).toList());
        }

        return model;
    }

    private OrderItem toOrderItemModel(OrderItemEntity entity) {
        OrderItem model = new OrderItem();
        model.setId(entity.getId());
        model.setQuantity(entity.getQuantity());
        model.setPrice(entity.getPrice());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getProduct() != null) {
            com.app.ecom.mono.model.Product product = new com.app.ecom.mono.model.Product();
            product.setId(entity.getProduct().getId());
            product.setName(entity.getProduct().getName());
            product.setPrice(entity.getProduct().getPrice());
            product.setStockQuantity(entity.getProduct().getStockQuantity());
            product.setCategory(entity.getProduct().getCategory());
            product.setImageUrl(entity.getProduct().getImageUrl());
            product.setActive(entity.getProduct().isActive());
            product.setCreatedAt(entity.getProduct().getCreatedAt());
            product.setUpdatedAt(entity.getProduct().getUpdatedAt());
            model.setProduct(product);
        }

        return model;
    }
}
