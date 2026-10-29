package com.inventienda.service;

import java.util.List;

import com.inventienda.dto.CategoriaRequest;
import com.inventienda.dto.CategoriaResponse;

public interface CategoriaService {

    CategoriaResponse crear(CategoriaRequest request);

    List<CategoriaResponse> listar();

    CategoriaResponse obtener(Long id);

    CategoriaResponse actualizar(Long id, CategoriaRequest request);

    void eliminar(Long id);
}
