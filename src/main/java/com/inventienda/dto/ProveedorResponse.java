package com.inventienda.dto;

import com.inventienda.model.Proveedor;

public record ProveedorResponse(Long id, String nombre, String telefono, String email) {

    public static ProveedorResponse from(Proveedor proveedor) {
        return new ProveedorResponse(proveedor.getId(), proveedor.getNombre(),
                proveedor.getTelefono(), proveedor.getEmail());
    }
}
