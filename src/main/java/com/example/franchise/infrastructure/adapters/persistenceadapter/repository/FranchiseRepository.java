package com.example.franchise.infrastructure.adapters.persistenceadapter.repository;

import com.example.franchise.infrastructure.adapters.persistenceadapter.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FranchiseRepository extends ReactiveCrudRepository<FranchiseEntity, Long> {
    Mono<Boolean> existsByName(String name);
}
