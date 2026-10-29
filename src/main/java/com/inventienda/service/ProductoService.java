package com.inventienda.service;

import java.util.List;

import com.inventienda.dto.ProductoRequest;
import com.inventienda.dto.ProductoResponse;

public interface ProductoService {

    ProductoResponse crear(ProductoRequest request);

    List<ProductoResponse> listarActivos();

    List<ProductoResponse> listarConStockBajo();

    ProductoResponse obtener(Long id);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    ProductoResponse reponer(Long id, int cantidad);

    void desactivar(Long id);
}
