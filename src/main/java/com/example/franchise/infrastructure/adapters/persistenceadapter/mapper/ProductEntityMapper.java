package com.example.franchise.infrastructure.adapters.persistenceadapter.mapper;

import com.example.franchise.domain.model.Product;
import com.example.franchise.infrastructure.adapters.persistenceadapter.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

    Product toModel (ProductEntity product);
    ProductEntity toEntity (Product model);
}
