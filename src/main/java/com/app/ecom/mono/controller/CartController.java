package com.app.ecom.mono.controller;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.ecom.mono.model.CartItem;
import com.app.ecom.mono.service.ICartItemService;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final ICartItemService cartService;

    public CartController(ICartItemService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<?> addCartItem(@RequestHeader("X-User-Id") Long userId,
                                         @PathVariable Long productId,
                                         @RequestParam Integer quantity) {
        try {
            CartItem item = cartService.addCartItem(userId, productId, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("internal error");
        }
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader("X-User-Id") Long userId) {
        try {
            return ResponseEntity.ok(cartService.getCartForUser(userId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("internal error");
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteCartItem(@RequestHeader("X-User-Id") Long userId,
                                            @PathVariable Long productId) {
        try {
            boolean deleted = cartService.deleteCartItem(userId, productId);
            if (!deleted) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("internal error");
        }
    }

}
