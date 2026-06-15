package com.app.ecom.mono.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.app.ecom.mono.Repo.IProductRepo;
import com.app.ecom.mono.entity.ProductEntity;
import com.app.ecom.mono.model.Product;

@Service
public class ProductService implements IProductService {

    private final IProductRepo productRepo;

    public ProductService(IProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findByActiveTrue().stream().map(this::toProduct).toList();
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        List<ProductEntity> entities;

        if (keyword == null || keyword.isBlank()) {
            entities = productRepo.findByActiveTrueAndStockQuantityGreaterThan(0);
        } else {
            entities = productRepo.findByActiveTrueAndStockQuantityGreaterThanAndNameContainingIgnoreCase(0, keyword.trim());
        }

        return entities.stream().map(this::toProduct).toList();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepo.findById(id).map(this::toProduct);
    }

    @Override
    public Product createProduct(Product product) {
        ProductEntity entity = toEntity(product);
        entity.setId(null);
        return toProduct(productRepo.save(entity));
    }

    @Override
    public Optional<Product> updateProduct(Long id, Product product) {
        if (!productRepo.existsById(id)) {
            return Optional.empty();
        }

        ProductEntity entity = toEntity(product);
        entity.setId(id);
        return Optional.of(toProduct(productRepo.save(entity)));
    }

    @Override
    public boolean deleteProduct(Long id) {
        if (!productRepo.existsById(id)) {
            return false;
        }

        productRepo.deleteById(id);
        return true;
    }

    private Product toProduct(ProductEntity entity) {
        Product product = new Product();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setPrice(entity.getPrice());
        product.setStockQuantity(entity.getStockQuantity());
        product.setCategory(entity.getCategory());
        product.setImageUrl(entity.getImageUrl());
        product.setActive(entity.isActive());
        product.setCreatedAt(entity.getCreatedAt());
        product.setUpdatedAt(entity.getUpdatedAt());
        return product;
    }

    private ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setName(product.getName());
        entity.setPrice(product.getPrice());
        entity.setStockQuantity(product.getStockQuantity());
        entity.setCategory(product.getCategory());
        entity.setImageUrl(product.getImageUrl());
        entity.setActive(product.isActive());
        entity.setCreatedAt(product.getCreatedAt());
        entity.setUpdatedAt(product.getUpdatedAt());
        return entity;
    }
}
