package com.example.franchise.domain.usecase;

import com.example.franchise.domain.api.ProductServicePort;
import com.example.franchise.domain.constants.Constants;
import com.example.franchise.domain.enums.TechnicalMessage;
import com.example.franchise.domain.exceptions.BusinessException;
import com.example.franchise.domain.model.Product;
import com.example.franchise.domain.spi.BranchPersistencePort;
import com.example.franchise.domain.spi.ProductPersistencePort;
import reactor.core.publisher.Mono;

public class ProductUseCase implements ProductServicePort {

    private final ProductPersistencePort productPort;
    private final BranchPersistencePort branchPort;

    public ProductUseCase(ProductPersistencePort productPort, BranchPersistencePort branchPort) {
        this.productPort = productPort;
        this.branchPort = branchPort;
    }

    @Override
    public Mono<Product> createProduct(Long branchId, Product product) {
        Product normalized = normalize(branchId, product);
                return validate(branchId, normalized)
                .then(branchPort.existsById(branchId))
                .flatMap(exists -> exists
                        ? Mono.just(true)
                        : Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND)))
                        .then(productPort.existsByBranchAndName(branchId, normalized.name()))
                .flatMap(exists -> exists
                        ? Mono.error(new BusinessException(TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                        : productPort.save(normalized));
    }

    @Override
    public Mono<Void> deleteProduct(Long branchId, Long productId) {
        return validateIds(branchId, productId)
                .then(branchPort.existsById(branchId))
                .flatMap(exists -> exists
                        ? Mono.just(true)
                        : Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND)))
                .then(productPort.findById(productId))
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND)))
                .flatMap(product -> {
                    if (!branchId.equals(product.branchId())) {
                        return Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_IN_BRANCH));
                    }
                    return productPort.deleteById(productId);
                });
    }

    @Override
    public Mono<Product> updateStock(Long branchId, Long productId, Integer newStock) {
        return validateIds(branchId, productId)
                .then(validateStock(newStock))
                .then(branchPort.existsById(branchId))
                .flatMap(exists -> exists
                        ? Mono.just(true)
                        : Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND)))
                .then(productPort.findById(productId))
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND)))
                .flatMap(product -> {
                    if (!branchId.equals(product.branchId())) {
                        return Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_IN_BRANCH));
                    }
                    return productPort.updateStock(productId, newStock);
                });
    }

    private Mono<Void> validateIds(Long branchId, Long productId) {
        if (branchId == null || branchId <= 0) {
            return Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND));
        }
        if (productId == null || productId <= 0) {
            return Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND));
        }
        return Mono.empty();
    }

    private Mono<Void> validateStock(Integer stock) {
        if (stock == null || stock < Constants.STOCK_MIN) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_STOCK));
        }
        return Mono.empty();
    }
    private Mono<Void> validate(Long branchId, Product product) {
        if (branchId == null || branchId <= 0) {
            return Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND));
        }
        if (product == null || Constants.isBlank(product.name())) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_PRODUCT_NAME));
        }
        if (product.name().length() > Constants.NAME_MAX_LENGTH) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_PRODUCT_NAME));
        }
        if (product.branchId() == null || !product.branchId().equals(branchId)) {
            return Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND));
        }
        if (product.stock() == null || product.stock() < Constants.STOCK_MIN) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_STOCK));
        }
        return Mono.empty();
    }

    private Product normalize(Long branchId, Product product) {
                if (product == null) return null;
                String trimmedName = product.name() == null ? null : product.name().trim();
                Integer stock = product.stock();
                if (stock == null) stock = Constants.STOCK_MIN;
                return new Product(product.id(), branchId, trimmedName, stock);
            }
}
