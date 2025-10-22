package com.example.franchise.infrastructure.adapters.persistenceadapter.repository;

import com.example.franchise.infrastructure.adapters.persistenceadapter.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {
    Mono<Boolean> existsByBranchIdAndName(Long branchId, String name);
    Mono<ProductEntity> findFirstByBranchIdOrderByStockDesc(Long branchId);
}
