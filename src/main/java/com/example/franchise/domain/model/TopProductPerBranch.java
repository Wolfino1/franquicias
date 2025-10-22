package com.example.franchise.domain.model;

public record TopProductPerBranch(
        Long branchId,
        String branchName,
        Long productId,
        String productName,
        Integer stock
) {}
