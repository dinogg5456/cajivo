package com.inventienda.dto;

import java.math.BigDecimal;

import com.inventienda.model.DetalleVenta;

public record DetalleVentaResponse(
        Long productoId,
        String producto,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal) {

    public static DetalleVentaResponse from(DetalleVenta d) {
        return new DetalleVentaResponse(
                d.getProducto().getId(),
                d.getProducto().getNombre(),
                d.getCantidad(),
                d.getPrecioUnitario(),
                d.getSubtotal());
    }
}
