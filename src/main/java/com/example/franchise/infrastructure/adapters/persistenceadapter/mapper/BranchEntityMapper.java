package com.example.franchise.infrastructure.adapters.persistenceadapter.mapper;

import com.example.franchise.domain.model.Branch;
import com.example.franchise.infrastructure.adapters.persistenceadapter.entity.BranchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchEntityMapper {

    Branch toModel(BranchEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BranchEntity toEntity(Branch model);
}
