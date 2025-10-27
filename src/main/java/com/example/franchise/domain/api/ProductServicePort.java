package com.example.franchise.domain.api;

import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.model.Product;
import reactor.core.publisher.Mono;

public interface ProductServicePort {
    Mono <Product> createProduct (Long branchId, Product product);
    Mono<Void> deleteProduct(Long branchId, Long productId);
    Mono<Product> updateStock(Long branchId, Long productId, Integer newStock);
    Mono<Product> renameProduct(Long id, String newName);
}
