package com.example.franchise.infrastructure.entrypoints.mapper;

import com.example.franchise.domain.model.TopProductPerBranch;
import com.example.franchise.infrastructure.entrypoints.dto.TopProductPerBranchDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TopProductPerBranchMapper {

    @Mapping(target = "branchId", source = "branchId")
    @Mapping(target = "branchName", source = "branchName")
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "stock", source = "stock")
    TopProductPerBranchDTO toDto(TopProductPerBranch model);
}
