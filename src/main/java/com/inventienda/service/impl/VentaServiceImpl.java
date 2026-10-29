package com.inventienda.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventienda.dto.ItemVentaRequest;
import com.inventienda.dto.VentaRequest;
import com.inventienda.dto.VentaResponse;
import com.inventienda.exception.RecursoNoEncontradoException;
import com.inventienda.exception.ReglaNegocioException;
import com.inventienda.model.DetalleVenta;
import com.inventienda.model.EstadoVenta;
import com.inventienda.model.Producto;
import com.inventienda.model.Venta;
import com.inventienda.repository.ClienteRepository;
import com.inventienda.repository.ProductoRepository;
import com.inventienda.repository.VentaRepository;
import com.inventienda.service.VentaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public VentaResponse registrar(VentaRequest request) {
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setEstado(EstadoVenta.REGISTRADA);
        if (request.clienteId() != null) {
            venta.setCliente(clienteRepository.findById(request.clienteId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", request.clienteId())));
        }

        BigDecimal total = BigDecimal.ZERO;
        for (ItemVentaRequest item : request.items()) {
            DetalleVenta detalle = crearDetalle(item);
            venta.agregarDetalle(detalle);
            total = total.add(detalle.getSubtotal());
        }
        venta.setTotal(total);

        return VentaResponse.from(ventaRepository.save(venta));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponse> listar() {
        return ventaRepository.findAllByOrderByFechaDesc().stream().map(VentaResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponse obtener(Long id) {
        return VentaResponse.from(buscar(id));
    }

    @Override
    @Transactional
    public VentaResponse anular(Long id) {
        Venta venta = buscar(id);
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new ReglaNegocioException("La venta " + id + " ya fue anulada");
        }
        venta.getDetalles().forEach(d -> d.getProducto().reponerStock(d.getCantidad()));
        venta.setEstado(EstadoVenta.ANULADA);
        return VentaResponse.from(venta);
    }

    private DetalleVenta crearDetalle(ItemVentaRequest item) {
        Producto producto = productoRepository.findById(item.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", item.productoId()));
        if (!producto.isActivo()) {
            throw new ReglaNegocioException("El producto '" + producto.getNombre() + "' está inactivo");
        }
        producto.descontarStock(item.cantidad());

        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(item.cantidad());
        detalle.setPrecioUnitario(producto.getPrecio());
        detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(item.cantidad())));
        return detalle;
    }

    private Venta buscar(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", id));
    }
}
