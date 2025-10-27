package com.example.franchise.infrastructure.entrypoints;

import com.example.franchise.infrastructure.entrypoints.handler.BranchHandler;
import com.example.franchise.infrastructure.entrypoints.handler.FranchiseHandler;
import com.example.franchise.infrastructure.entrypoints.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FranchiseRouter {

    @Bean
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler franchiseHandler,
                                                          BranchHandler branchHandler,
                                                          ProductHandler productHandler) {
        return route(POST("/franchises"), franchiseHandler::createFranchise)
                .andRoute(POST("/franchises/{franchiseId}/branches"), branchHandler::createBranch)
                .andRoute(POST("/branches/{branchId}/products"), productHandler::createProduct)
                .andRoute(DELETE("/branches/{branchId}/products/{productId}"), productHandler::deleteProduct)
                .andRoute(PATCH("/branches/{branchId}/products/{productId}/stock"), productHandler::updateStock)
                .andRoute(GET("/franchises/{franchiseId}/top-products"), franchiseHandler::getTopProductsByBranch)
                .andRoute(PATCH("/franchises/{id}/name"), franchiseHandler::renameFranchise)
                .andRoute(PATCH("/branches/{id}/name"), branchHandler::renameBranch)
                .andRoute(PATCH("/products/{id}/name"), productHandler::renameProduct);
    }
}
