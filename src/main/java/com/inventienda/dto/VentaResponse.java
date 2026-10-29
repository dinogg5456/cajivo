package com.inventienda.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.inventienda.model.EstadoVenta;
import com.inventienda.model.Venta;

public record VentaResponse(
        Long id,
        LocalDateTime fecha,
        EstadoVenta estado,
        BigDecimal total,
        String cliente,
        List<DetalleVentaResponse> detalles) {

    public static VentaResponse from(Venta venta) {
        return new VentaResponse(
                venta.getId(),
                venta.getFecha(),
                venta.getEstado(),
                venta.getTotal(),
                venta.getCliente() == null ? null : venta.getCliente().getNombre(),
                venta.getDetalles().stream().map(DetalleVentaResponse::from).toList());
    }
}
