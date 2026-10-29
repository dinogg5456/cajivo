package com.inventienda.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record VentaRequest(
        Long clienteId,

        @NotEmpty(message = "La venta debe tener al menos un producto")
        List<@Valid ItemVentaRequest> items) {
}
