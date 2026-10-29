package com.inventienda.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemVentaRequest(
        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        int cantidad) {
}
