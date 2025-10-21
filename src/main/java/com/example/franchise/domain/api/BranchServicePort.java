package com.example.franchise.domain.api;

import com.example.franchise.domain.model.Branch;
import reactor.core.publisher.Mono;

public interface BranchServicePort {
    Mono<Branch> createBranch(Long franchiseId, Branch branch);
}
