package com.inventienda.service;

import java.util.List;

import com.inventienda.dto.ClienteRequest;
import com.inventienda.dto.ClienteResponse;

public interface ClienteService {

    ClienteResponse crear(ClienteRequest request);

    List<ClienteResponse> listar();

    ClienteResponse obtener(Long id);

    ClienteResponse actualizar(Long id, ClienteRequest request);

    void eliminar(Long id);
}
