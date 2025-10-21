package com.example.franchise.infrastructure.entrypoints.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductCreateDTO {
    @NotBlank(message = "name must not be blank")
    @Size(max = 80, message = "name must be at most 80 characters")
    private String name;
    private Integer stock;
}
