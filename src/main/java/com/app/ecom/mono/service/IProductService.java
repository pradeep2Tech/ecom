package com.app.ecom.mono.service;

import java.util.List;
import java.util.Optional;

import com.app.ecom.mono.model.Product;

public interface IProductService {

    List<Product> getAllProducts();

    List<Product> searchProducts(String keyword);

    Optional<Product> getProductById(Long id);

    Product createProduct(Product product);

    Optional<Product> updateProduct(Long id, Product product);

    boolean deleteProduct(Long id);
}
