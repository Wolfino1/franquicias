package com.example.franchise.domain.spi;

import com.example.franchise.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchPersistencePort {
    Mono<Boolean> existsByFranchiseAndName(Long franchiseId, String name);
    Mono<Branch> save(Branch branch);
    Mono<Boolean> existsById(Long branchId);
    Flux<Branch> findByFranchiseId(Long franchiseId);
}
