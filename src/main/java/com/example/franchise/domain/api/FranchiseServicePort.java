package com.example.franchise.domain.api;

import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.model.TopProductPerBranch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseServicePort {
    Mono<Franchise> createFranchise(Franchise franchise);
    Flux<TopProductPerBranch> getTopProductsByBranch(Long franchiseId);
    Mono<Franchise> renameFranchise(Long id, String newName);
}
