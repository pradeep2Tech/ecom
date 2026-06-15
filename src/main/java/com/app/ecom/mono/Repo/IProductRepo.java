package com.app.ecom.mono.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.ecom.mono.entity.ProductEntity;

@Repository
public interface IProductRepo extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByActiveTrue();

    List<ProductEntity> findByActiveTrueAndStockQuantityGreaterThan(Integer stockQuantity);

    List<ProductEntity> findByActiveTrueAndStockQuantityGreaterThanAndNameContainingIgnoreCase(Integer stockQuantity, String keyword);
}
