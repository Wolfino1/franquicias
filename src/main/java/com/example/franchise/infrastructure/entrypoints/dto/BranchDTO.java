package com.example.franchise.infrastructure.entrypoints.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BranchDTO {
    private Long id;
    private Long franchiseId;
    @NotBlank(message = "name must not be blank")
    @Size(max = 80, message = "name must be at most 80 characters")
    private String name;
}
