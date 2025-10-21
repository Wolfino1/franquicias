package com.example.franchise.infrastructure.adapters.persistenceadapter.repository;

import com.example.franchise.infrastructure.adapters.persistenceadapter.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BranchRepository extends ReactiveCrudRepository<BranchEntity, Long> {
    Mono<Boolean> existsByFranchiseIdAndName(Long franchiseId, String name);
}
