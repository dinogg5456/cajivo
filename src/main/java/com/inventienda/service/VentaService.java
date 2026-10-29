package com.inventienda.service;

import java.util.List;

import com.inventienda.dto.VentaRequest;
import com.inventienda.dto.VentaResponse;

public interface VentaService {

    VentaResponse registrar(VentaRequest request);

    List<VentaResponse> listar();

    VentaResponse obtener(Long id);

    VentaResponse anular(Long id);
}
