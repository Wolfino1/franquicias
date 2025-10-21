package com.example.franchise.infrastructure.adapters.persistenceadapter;

import com.example.franchise.domain.model.Product;
import com.example.franchise.domain.spi.ProductPersistencePort;
import com.example.franchise.infrastructure.adapters.persistenceadapter.mapper.ProductEntityMapper;
import com.example.franchise.infrastructure.adapters.persistenceadapter.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPersistencePort {
    private final ProductEntityMapper mapper;
    private final ProductRepository repository;


    @Override
    public Mono<Boolean> existsByBranchAndName(Long branchId, String name) {
        return repository.existsByBranchIdAndName(branchId, name);
    }

    @Override
    public Mono<Product> save(Product product) {
        return repository.save(mapper.toEntity(product))
                .map(mapper::toModel);
    }
}
