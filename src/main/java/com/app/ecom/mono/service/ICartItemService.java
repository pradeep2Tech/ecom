package com.app.ecom.mono.service;

import java.util.List;

import com.app.ecom.mono.model.CartItem;

public interface ICartItemService {

    CartItem addCartItem(Long userId, Long productId, Integer quantity);

    java.util.List<CartItem> getCartForUser(Long userId);

    boolean deleteCartItem(Long userId, Long productId);

}
