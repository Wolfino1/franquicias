package com.example.franchise.infrastructure.adapters.persistenceadapter.mapper;

import com.example.franchise.domain.model.Franchise;
import com.example.franchise.infrastructure.adapters.persistenceadapter.entity.FranchiseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FranchiseEntityMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    Franchise toModel(FranchiseEntity entity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    FranchiseEntity toEntity(Franchise model);
}
