package com.example.franchise.domain.usecase;

import com.example.franchise.domain.api.FranchiseServicePort;
import com.example.franchise.domain.constants.Constants;
import com.example.franchise.domain.enums.TechnicalMessage;
import com.example.franchise.domain.exceptions.BusinessException;
import com.example.franchise.domain.model.Branch;
import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.model.Product;
import com.example.franchise.domain.model.TopProductPerBranch;
import com.example.franchise.domain.spi.BranchPersistencePort;
import com.example.franchise.domain.spi.FranchisePersistencePort;
import com.example.franchise.domain.spi.ProductPersistencePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FranchiseUseCase implements FranchiseServicePort {

    private final FranchisePersistencePort franchisePort;
    private final BranchPersistencePort branchPort;
    private final ProductPersistencePort productPort;

    public FranchiseUseCase(FranchisePersistencePort franchisePort,
                            BranchPersistencePort branchPort,
                            ProductPersistencePort productPort) {
        this.franchisePort = franchisePort;
        this.branchPort = branchPort;
        this.productPort = productPort;
    }

    @Override
    public Mono<Franchise> createFranchise(Franchise franchise) {
        return validate(franchise)
                .then(franchisePort.existsByName(franchise.name()))
                .flatMap(exists -> exists
                        ? Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                        : franchisePort.save(franchise)
                );
    }

    @Override
    public Flux<TopProductPerBranch> getTopProductsByBranch(Long franchiseId) {
        if (franchiseId == null || franchiseId <= 0) {
            return Flux.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND));
        }



        return franchisePort.existsById(franchiseId)
                .flatMapMany(exists -> exists
                        ? branchPort.findByFranchiseId(franchiseId)
                        : Flux.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND))
                )
                .flatMap(branch ->
                        productPort.findTopByBranchIdOrderByStockDesc(branch.id())
                                .map(prod -> toTop(branch, prod))
                                .switchIfEmpty(Mono.empty()) // si no hay productos en la sucursal, no se emite nada
                );
    }

    @Override
    public Mono<Franchise> renameFranchise(Long id, String newName) {
        if (id == null || id <= 0) {
            return Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND));
        }
        if (Constants.isBlank(newName) || newName.length() > Constants.NAME_MAX_LENGTH) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_FRANCHISE_NAME));
        }

        return franchisePort.existsById(id)
                .flatMap(exists -> exists
                        ? Mono.just(true)
                        : Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND)))
                .then(franchisePort.existsByName(newName))
                .flatMap(dup -> dup
                        ? Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                        : franchisePort.updateName(id, newName));
    }


    private TopProductPerBranch toTop(Branch branch, Product product) {
        return new TopProductPerBranch(
                branch.id(),
                branch.name(),
                product.id(),
                product.name(),
                product.stock()
        );
    }

    private Mono<Void> validate(Franchise franchise) {
        if (franchise == null || Constants.isBlank(franchise.name())) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_FRANCHISE_NAME));
        }
        if (franchise.name().length() > Constants.NAME_MAX_LENGTH) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_FRANCHISE_NAME));
        }
        return Mono.empty();
    }
}
