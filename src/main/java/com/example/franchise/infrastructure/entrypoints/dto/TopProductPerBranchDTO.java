package com.example.franchise.infrastructure.entrypoints.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TopProductPerBranchDTO {
    private Long branchId;
    private String branchName;
    private Long productId;
    private String productName;
    private Integer stock;
}
