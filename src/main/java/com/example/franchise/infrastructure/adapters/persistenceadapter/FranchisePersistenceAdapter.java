package com.example.franchise.infrastructure.adapters.persistenceadapter;

import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.spi.FranchisePersistencePort;
import com.example.franchise.infrastructure.adapters.persistenceadapter.mapper.FranchiseEntityMapper;
import com.example.franchise.infrastructure.adapters.persistenceadapter.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchisePersistenceAdapter implements FranchisePersistencePort {

    private final FranchiseRepository repository;
    private final FranchiseEntityMapper mapper;

    @Override
    public Mono<Boolean> existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return repository.save(mapper.toEntity(franchise))
                .map(mapper::toModel);
    }

    @Override
    public Mono<Boolean> existsById(Long franchiseId) {
        return repository.existsById(franchiseId);
    }
}
