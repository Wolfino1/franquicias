package com.example.franchise.domain.model;

public record Product(Long id, Long branchId, String name, Integer stock) {
}
