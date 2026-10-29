package com.inventienda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre admite máximo 80 caracteres")
        String nombre,

        @Size(max = 200, message = "La descripción admite máximo 200 caracteres")
        String descripcion) {
}
