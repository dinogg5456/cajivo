package com.inventienda.dto;

import com.inventienda.model.Cliente;

public record ClienteResponse(Long id, String documento, String nombre, String telefono) {

    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getDocumento(),
                cliente.getNombre(), cliente.getTelefono());
    }
}
