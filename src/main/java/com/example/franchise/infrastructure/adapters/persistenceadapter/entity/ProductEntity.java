package com.example.franchise.infrastructure.adapters.persistenceadapter.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class ProductEntity {

    @Id
    private Long id;

    @Column("branch_id")
    private Long branchId;

    private String name;

    private Integer stock;

}
