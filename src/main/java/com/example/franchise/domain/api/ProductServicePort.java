package com.example.franchise.domain.api;

import com.example.franchise.domain.model.Product;
import reactor.core.publisher.Mono;

public interface ProductServicePort {
    Mono <Product> createProduct (Long branchId, Product product);
}
