package com.inventienda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProveedorRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre admite máximo 120 caracteres")
        String nombre,

        @Size(max = 20, message = "El teléfono admite máximo 20 caracteres")
        String telefono,

        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 120, message = "El correo admite máximo 120 caracteres")
        String email) {
}
