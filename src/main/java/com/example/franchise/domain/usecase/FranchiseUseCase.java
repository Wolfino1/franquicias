package com.example.franchise.domain.usecase;

import com.example.franchise.domain.api.FranchiseServicePort;
import com.example.franchise.domain.constants.Constants;
import com.example.franchise.domain.enums.TechnicalMessage;
import com.example.franchise.domain.exceptions.BusinessException;
import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.spi.FranchisePersistencePort;
import reactor.core.publisher.Mono;

public class FranchiseUseCase implements FranchiseServicePort {

    private final FranchisePersistencePort franchisePersistencePort;

    public FranchiseUseCase(FranchisePersistencePort franchisePersistencePort) {
        this.franchisePersistencePort = franchisePersistencePort;
    }

    @Override
    public Mono<Franchise> createFranchise(Franchise franchise) {
        return validate(franchise)
                .then(franchisePersistencePort.existsByName(franchise.name()))
                .flatMap(exists -> exists
                        ? Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                        : franchisePersistencePort.save(franchise)
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
