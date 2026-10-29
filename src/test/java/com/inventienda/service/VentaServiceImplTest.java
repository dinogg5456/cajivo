package com.inventienda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventienda.dto.ItemVentaRequest;
import com.inventienda.dto.VentaRequest;
import com.inventienda.dto.VentaResponse;
import com.inventienda.exception.RecursoNoEncontradoException;
import com.inventienda.exception.ReglaNegocioException;
import com.inventienda.exception.StockInsuficienteException;
import com.inventienda.model.Categoria;
import com.inventienda.model.DetalleVenta;
import com.inventienda.model.EstadoVenta;
import com.inventienda.model.Producto;
import com.inventienda.model.Venta;
import com.inventienda.repository.ClienteRepository;
import com.inventienda.repository.ProductoRepository;
import com.inventienda.repository.VentaRepository;
import com.inventienda.service.impl.VentaServiceImpl;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private VentaServiceImpl service;

    private Producto producto(int stock) {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Granos");

        Producto p = new Producto();
        p.setId(1L);
        p.setCodigo("P-001");
        p.setNombre("Arroz 1kg");
        p.setPrecio(new BigDecimal("2500.00"));
        p.setStock(stock);
        p.setStockMinimo(2);
        p.setCategoria(categoria);
        return p;
    }

    private VentaRequest ventaDe(long productoId, int cantidad) {
        return new VentaRequest(null, List.of(new ItemVentaRequest(productoId, cantidad)));
    }

    @Test
    @DisplayName("Registrar una venta descuenta el stock y calcula el total")
    void registrar_ventaValida_descuentaStockYCalculaTotal() {
        Producto arroz = producto(10);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(arroz));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        VentaResponse respuesta = service.registrar(ventaDe(1L, 3));

        assertEquals(0, new BigDecimal("7500.00").compareTo(respuesta.total()));
        assertEquals(7, arroz.getStock());
        assertEquals(EstadoVenta.REGISTRADA, respuesta.estado());
        assertEquals(1, respuesta.detalles().size());
    }

    @Test
    @DisplayName("Registrar con stock insuficiente lanza excepción y no guarda la venta")
    void registrar_stockInsuficiente_lanzaExcepcion() {
        Producto arroz = producto(2);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(arroz));

        assertThrows(StockInsuficienteException.class, () -> service.registrar(ventaDe(1L, 5)));

        assertEquals(2, arroz.getStock());
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    @DisplayName("Registrar con un producto inexistente lanza RecursoNoEncontradoException")
    void registrar_productoInexistente_lanzaExcepcion() {
        when(productoRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.registrar(ventaDe(5L, 1)));
    }

    @Test
    @DisplayName("Registrar con un producto inactivo viola la regla de negocio")
    void registrar_productoInactivo_lanzaExcepcion() {
        Producto arroz = producto(10);
        arroz.setActivo(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(arroz));

        assertThrows(ReglaNegocioException.class, () -> service.registrar(ventaDe(1L, 1)));

        assertEquals(10, arroz.getStock());
    }

    @Test
    @DisplayName("Anular una venta devuelve las unidades al inventario")
    void anular_ventaRegistrada_restituyeStock() {
        Producto arroz = producto(7);
        Venta venta = ventaRegistrada(arroz, 3);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        VentaResponse respuesta = service.anular(1L);

        assertEquals(10, arroz.getStock());
        assertEquals(EstadoVenta.ANULADA, respuesta.estado());
    }

    @Test
    @DisplayName("Anular dos veces la misma venta lanza excepción y no repone stock otra vez")
    void anular_ventaYaAnulada_lanzaExcepcion() {
        Producto arroz = producto(10);
        Venta venta = ventaRegistrada(arroz, 3);
        venta.setEstado(EstadoVenta.ANULADA);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> service.anular(1L));

        assertTrue(ex.getMessage().contains("ya fue anulada"));
        assertEquals(10, arroz.getStock());
    }

    private Venta ventaRegistrada(Producto producto, int cantidad) {
        BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));

        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(producto.getPrecio());
        detalle.setSubtotal(subtotal);

        Venta venta = new Venta();
        venta.setId(1L);
        venta.setFecha(LocalDateTime.now());
        venta.setEstado(EstadoVenta.REGISTRADA);
        venta.setTotal(subtotal);
        venta.agregarDetalle(detalle);
        return venta;
    }
}
