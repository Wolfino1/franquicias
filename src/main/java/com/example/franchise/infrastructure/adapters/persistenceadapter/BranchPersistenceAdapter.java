package com.example.franchise.infrastructure.adapters.persistenceadapter;

import com.example.franchise.domain.model.Branch;
import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.spi.BranchPersistencePort;
import com.example.franchise.infrastructure.adapters.persistenceadapter.mapper.BranchEntityMapper;
import com.example.franchise.infrastructure.adapters.persistenceadapter.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchPersistenceAdapter implements BranchPersistencePort {

    private final BranchRepository repository;
    private final BranchEntityMapper mapper;

    @Override
    public Mono<Boolean> existsByFranchiseAndName(Long franchiseId, String name) {
        return repository.existsByFranchiseIdAndName(franchiseId, name);
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        return repository.save(mapper.toEntity(branch))
                .map(mapper::toModel);
    }

    @Override
    public Mono<Boolean> existsById(Long branchId) {
        return repository.existsById(branchId);
    }

    @Override
    public Flux<Branch> findByFranchiseId(Long franchiseId) {
        return repository.findByFranchiseId(franchiseId)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Branch> updateName(Long id, String newName) {
        return repository.findById(id)
                .switchIfEmpty(Mono.empty())
                .flatMap(entity -> {
                    entity.setName(newName);
                    return repository.save(entity);
                })
                .map(mapper::toModel);
    }
}
