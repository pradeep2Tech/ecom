package com.app.ecom.mono.service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.app.ecom.mono.Repo.ICartItemRepo;
import com.app.ecom.mono.Repo.IProductRepo;
import com.app.ecom.mono.Repo.IUserRepo;
import com.app.ecom.mono.entity.CartItemEntity;
import com.app.ecom.mono.entity.ProductEntity;
import com.app.ecom.mono.entity.UserEntity;
import com.app.ecom.mono.model.CartItem;
import com.app.ecom.mono.model.Product;

@Service
public class CartItemService implements ICartItemService {

    private final ICartItemRepo cartItemRepo;
    private final IUserRepo userRepo;
    private final IProductRepo productRepo;

    public CartItemService(ICartItemRepo cartItemRepo, IUserRepo userRepo, IProductRepo productRepo) {
        this.cartItemRepo = cartItemRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    @Override
    public CartItem addCartItem(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than 0");
        }

        UserEntity user = userRepo.findById(userId).orElseThrow(() -> new NoSuchElementException("user not found"));
        ProductEntity product = productRepo.findById(productId).orElseThrow(() -> new NoSuchElementException("product not found"));

        CartItemEntity existing = cartItemRepo.findByUserIdAndProductId(userId, productId).orElse(null);

        if (existing != null) {
            int newQty = existing.getQuantity() + quantity;
            existing.setQuantity(newQty);
            BigDecimal price = product.getPrice() == null ? BigDecimal.ZERO : product.getPrice().multiply(BigDecimal.valueOf(newQty));
            existing.setPrice(price);
            CartItemEntity saved = cartItemRepo.save(existing);
            return toModel(saved);
        }

        CartItemEntity entity = new CartItemEntity();
        entity.setUser(user);
        entity.setProduct(product);
        entity.setQuantity(quantity);
        BigDecimal price = product.getPrice() == null ? BigDecimal.ZERO : product.getPrice().multiply(BigDecimal.valueOf(quantity));
        entity.setPrice(price);

        CartItemEntity saved = cartItemRepo.save(entity);
        return toModel(saved);
    }

    @Override
    public java.util.List<CartItem> getCartForUser(Long userId) {
        userRepo.findById(userId).orElseThrow(() -> new NoSuchElementException("user not found"));
        return cartItemRepo.findByUserId(userId).stream().map(this::toModel).toList();
    }

    @Override
    public boolean deleteCartItem(Long userId, Long productId) {
        userRepo.findById(userId).orElseThrow(() -> new NoSuchElementException("user not found"));
        java.util.Optional<CartItemEntity> opt = cartItemRepo.findByUserIdAndProductId(userId, productId);
        if (opt.isPresent()) {
            cartItemRepo.delete(opt.get());
            return true;
        }
        return false;
    }

    private CartItem toModel(CartItemEntity e) {
        CartItem m = new CartItem();
        m.setId(e.getId());
        m.setUserId(e.getUser() != null ? e.getUser().getId() : null);

        if (e.getProduct() != null) {
            Product p = new Product();
            p.setId(e.getProduct().getId());
            p.setName(e.getProduct().getName());
            p.setPrice(e.getProduct().getPrice());
            p.setStockQuantity(e.getProduct().getStockQuantity());
            p.setCategory(e.getProduct().getCategory());
            p.setImageUrl(e.getProduct().getImageUrl());
            p.setActive(e.getProduct().isActive());
            p.setCreatedAt(e.getProduct().getCreatedAt());
            p.setUpdatedAt(e.getProduct().getUpdatedAt());
            m.setProduct(p);
        }

        m.setQuantity(e.getQuantity());
        m.setPrice(e.getPrice());
        m.setCreatedAt(e.getCreatedAt());
        m.setUpdatedAt(e.getUpdatedAt());
        return m;
    }

}
