package com.example.franchise.domain.spi;

import com.example.franchise.domain.model.Product;
import reactor.core.publisher.Mono;

public interface ProductPersistencePort {
    Mono<Boolean> existsByBranchAndName(Long branchId, String name);
    Mono<Product> save(Product product);

}
