package com.inventienda.service;

import java.util.List;

import com.inventienda.dto.ProveedorRequest;
import com.inventienda.dto.ProveedorResponse;

public interface ProveedorService {

    ProveedorResponse crear(ProveedorRequest request);

    List<ProveedorResponse> listar();

    ProveedorResponse obtener(Long id);

    ProveedorResponse actualizar(Long id, ProveedorRequest request);

    void eliminar(Long id);
}
