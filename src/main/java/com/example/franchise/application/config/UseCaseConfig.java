package com.example.franchise.application.config;

import com.example.franchise.domain.api.BranchServicePort;
import com.example.franchise.domain.api.FranchiseServicePort;
import com.example.franchise.domain.api.ProductServicePort;
import com.example.franchise.domain.spi.BranchPersistencePort;
import com.example.franchise.domain.spi.ProductPersistencePort;
import com.example.franchise.domain.usecase.BranchUseCase;
import com.example.franchise.domain.usecase.FranchiseUseCase;
import com.example.franchise.domain.spi.FranchisePersistencePort;
import com.example.franchise.domain.usecase.ProductUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

        @Bean
        public FranchiseServicePort franchiseServicePort(FranchisePersistencePort franchisePort,
                                                         BranchPersistencePort branchPort,
                                                         ProductPersistencePort productPort) {
                return new FranchiseUseCase(franchisePort, branchPort, productPort);
        }

        @Bean
        public BranchServicePort branchServicePort(BranchPersistencePort branchPort,
                                                   FranchisePersistencePort franchisePort) {
                return new BranchUseCase(branchPort, franchisePort);
        }

        @Bean
        public ProductServicePort productServicePort(ProductPersistencePort productPort,
                                                     BranchPersistencePort branchPort) {
                return new ProductUseCase(productPort, branchPort);
        }
}
