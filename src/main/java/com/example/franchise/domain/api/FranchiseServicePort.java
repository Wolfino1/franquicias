package com.example.franchise.domain.api;

import com.example.franchise.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseServicePort {
    Mono<Franchise> createFranchise(Franchise franchise);
}
