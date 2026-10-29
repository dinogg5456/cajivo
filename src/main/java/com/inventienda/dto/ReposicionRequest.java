package com.inventienda.dto;

import jakarta.validation.constraints.Min;

public record ReposicionRequest(
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        int cantidad) {
}
