package com.app.ecom.mono.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.ecom.mono.entity.CartItemEntity;

@Repository
public interface ICartItemRepo extends JpaRepository<CartItemEntity, Long> {
	java.util.Optional<CartItemEntity> findByUserIdAndProductId(Long userId, Long productId);

	java.util.List<CartItemEntity> findByUserId(Long userId);
}
