package com.example.franchise.infrastructure.adapters.persistenceadapter;

import com.example.franchise.domain.model.Franchise;
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

    @Override
    public Mono<Product> findById(Long productId) {
        return repository.findById(productId)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Void> deleteById(Long productId) {
        return repository.deleteById(productId);
    }

    @Override
    public Mono<Product> updateStock(Long productId, Integer stock) {
        return repository.findById(productId)
                .flatMap(entity -> {
                    entity.setStock(stock);
                    return repository.save(entity);
                })
                .map(mapper::toModel);
    }
    @Override
    public Mono<Product> findTopByBranchIdOrderByStockDesc(Long branchId) {
        return repository.findFirstByBranchIdOrderByStockDesc(branchId)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Product> updateName(Long id, String newName) {
        return repository.findById(id)
                .switchIfEmpty(Mono.empty())
                .flatMap(entity -> {
                    entity.setName(newName);
                    return repository.save(entity);
                })
                .map(mapper::toModel);
    }
}
