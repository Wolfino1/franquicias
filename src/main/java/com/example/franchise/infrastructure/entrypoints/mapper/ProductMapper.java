package com.example.franchise.infrastructure.entrypoints.mapper;

import com.example.franchise.domain.model.Product;
import com.example.franchise.infrastructure.entrypoints.dto.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "branchId", source = "branchId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "stock", source = "stock")
    ProductDTO toDto(Product model);
}
