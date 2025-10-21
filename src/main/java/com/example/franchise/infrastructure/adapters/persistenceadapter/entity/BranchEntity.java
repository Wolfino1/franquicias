package com.example.franchise.infrastructure.adapters.persistenceadapter.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "branches")
@Getter
@Setter
@NoArgsConstructor
public class BranchEntity {
    @Id
    private Long id;

    @Column("franchise_id")
    private Long franchiseId;

    private String name;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
