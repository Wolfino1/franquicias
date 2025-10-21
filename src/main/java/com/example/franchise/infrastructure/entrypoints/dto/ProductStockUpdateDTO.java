package com.example.franchise.infrastructure.entrypoints.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductStockUpdateDTO {
    @NotNull(message = "stock must not be null")
    @Min(value = 0, message = "stock must be greater or equal to 0")
    private Integer stock;
}
