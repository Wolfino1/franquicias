package com.example.franchise.domain.api;

import com.example.franchise.domain.model.Branch;
import com.example.franchise.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface BranchServicePort {
    Mono<Branch> createBranch(Long franchiseId, Branch branch);
    Mono<Branch> renameBranch(Long id, String newName);
}
