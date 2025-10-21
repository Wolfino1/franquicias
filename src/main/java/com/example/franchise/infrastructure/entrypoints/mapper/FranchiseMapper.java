package com.example.franchise.infrastructure.entrypoints.mapper;

import com.example.franchise.domain.model.Franchise;
import com.example.franchise.infrastructure.entrypoints.dto.FranchiseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {

    // DTO -> Domain
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Franchise toModel(FranchiseDTO dto);

    // Domain -> DTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    FranchiseDTO toDto(Franchise model);
}
