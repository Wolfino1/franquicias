package com.example.franchise.infrastructure.entrypoints.mapper;

import com.example.franchise.domain.model.Branch;
import com.example.franchise.infrastructure.entrypoints.dto.BranchDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "franchiseId", source = "franchiseId")
    @Mapping(target = "name", source = "name")
    BranchDTO toDto(Branch model);
}
