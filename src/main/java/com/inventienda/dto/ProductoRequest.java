package com.inventienda.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductoRequest(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 30, message = "El código admite máximo 30 caracteres")
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre admite máximo 120 caracteres")
        String nombre,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
        BigDecimal precio,

        @Min(value = 0, message = "El stock no puede ser negativo")
        int stock,

        @Min(value = 0, message = "El stock mínimo no puede ser negativo")
        int stockMinimo,

        @NotNull(message = "La categoría es obligatoria")
        Long categoriaId,

        Long proveedorId) {
}
