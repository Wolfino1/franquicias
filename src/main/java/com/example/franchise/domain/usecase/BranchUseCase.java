package com.example.franchise.domain.usecase;

import com.example.franchise.domain.api.BranchServicePort;
import com.example.franchise.domain.constants.Constants;
import com.example.franchise.domain.enums.TechnicalMessage;
import com.example.franchise.domain.exceptions.BusinessException;
import com.example.franchise.domain.model.Branch;
import com.example.franchise.domain.spi.BranchPersistencePort;
import com.example.franchise.domain.spi.FranchisePersistencePort;
import reactor.core.publisher.Mono;

public class BranchUseCase implements BranchServicePort {

    private final BranchPersistencePort branchPort;
    private final FranchisePersistencePort franchisePort;

    public BranchUseCase(BranchPersistencePort branchPort, FranchisePersistencePort franchisePort) {
        this.branchPort = branchPort;
        this.franchisePort = franchisePort;
    }

    @Override
    public Mono<Branch> createBranch(Long franchiseId, Branch branch) {
        return validate(franchiseId, branch)
                .then(franchisePort.existsById(franchiseId))
                .flatMap(exists -> exists
                        ? Mono.just(true)
                        : Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND)))
                .then(branchPort.existsByFranchiseAndName(franchiseId, branch.name()))
                .flatMap(exists -> exists
                        ? Mono.error(new BusinessException(TechnicalMessage.BRANCH_ALREADY_EXISTS))
                        : branchPort.save(branch));
    }

    @Override
    public Mono<Branch> renameBranch(Long id, String newName) {
        if (id == null || id <= 0) {
            return Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND));
        }
        if (Constants.isBlank(newName) || newName.length() > Constants.NAME_MAX_LENGTH) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_BRANCH_NAME));
        }

        return branchPort.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND)))
                .flatMap(current -> {
                    if (newName.equals(current.name())) {
                        return Mono.just(current);
                    }
                    return branchPort.existsByFranchiseAndName(current.franchiseId(), newName)
                            .flatMap(dup -> dup
                                    ? Mono.error(new BusinessException(TechnicalMessage.BRANCH_ALREADY_EXISTS))
                                    : branchPort.updateName(id, newName));
                });
    }

    private Mono<Void> validate(Long franchiseId, Branch branch) {
        if (franchiseId == null || franchiseId <= 0) {
            return Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND));
        }
        if (branch == null || Constants.isBlank(branch.name())) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_BRANCH_NAME));
        }
        if (branch.name().length() > Constants.NAME_MAX_LENGTH) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_BRANCH_NAME));
        }
        if (branch.franchiseId() == null || !branch.franchiseId().equals(franchiseId)) {
            return Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND));
        }
        return Mono.empty();
    }
}
