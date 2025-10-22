package com.example.franchise.domain.spi;

import com.example.franchise.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface FranchisePersistencePort {
    Mono<Boolean> existsByName(String name);
    Mono<Franchise> save(Franchise franchise);
    Mono<Boolean> existsById(Long franchiseId);
    Mono<Franchise> updateName(Long id, String newName);
}
