package com.app.ecom.mono.service;

import java.util.Optional;

import com.app.ecom.mono.model.Order;

public interface IOrderService {

    Order createOrder(Long userId);

    Optional<Order> getOrderById(Long orderId);

    java.util.List<Order> getOrdersByUserId(Long userId);
}
