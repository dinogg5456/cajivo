package com.inventienda.dto;

import java.math.BigDecimal;

import com.inventienda.model.Producto;

public record ProductoResponse(
        Long id,
        String codigo,
        String nombre,
        BigDecimal precio,
        int stock,
        int stockMinimo,
        boolean activo,
        boolean stockBajo,
        String categoria,
        String proveedor) {

    public static ProductoResponse from(Producto p) {
        return new ProductoResponse(
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getPrecio(),
                p.getStock(),
                p.getStockMinimo(),
                p.isActivo(),
                p.tieneStockBajo(),
                p.getCategoria().getNombre(),
                p.getProveedor() == null ? null : p.getProveedor().getNombre());
    }
}
