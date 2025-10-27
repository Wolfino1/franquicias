package com.example.franchise.domain.spi;

import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.model.Product;
import reactor.core.publisher.Mono;

public interface ProductPersistencePort {
    Mono<Boolean> existsByBranchAndName(Long branchId, String name);
    Mono<Product> save(Product product);
    Mono<Product> findById(Long productId);
    Mono<Void> deleteById(Long productId);
    Mono<Product> updateStock(Long productId, Integer stock);
    Mono<Product> findTopByBranchIdOrderByStockDesc(Long branchId);
    Mono<Product> updateName(Long id, String newName);

}
