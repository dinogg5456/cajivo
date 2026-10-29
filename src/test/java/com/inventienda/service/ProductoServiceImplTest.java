package com.inventienda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventienda.dto.ProductoRequest;
import com.inventienda.dto.ProductoResponse;
import com.inventienda.exception.RecursoDuplicadoException;
import com.inventienda.exception.RecursoNoEncontradoException;
import com.inventienda.model.Categoria;
import com.inventienda.model.Producto;
import com.inventienda.repository.CategoriaRepository;
import com.inventienda.repository.ProductoRepository;
import com.inventienda.repository.ProveedorRepository;
import com.inventienda.service.impl.ProductoServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProductoServiceImpl service;

    private Categoria categoria() {
        Categoria c = new Categoria();
        c.setId(1L);
        c.setNombre("Granos");
        return c;
    }

    private Producto producto(int stock, int stockMinimo) {
        Producto p = new Producto();
        p.setId(1L);
        p.setCodigo("P-001");
        p.setNombre("Arroz 1kg");
        p.setPrecio(new BigDecimal("2500.00"));
        p.setStock(stock);
        p.setStockMinimo(stockMinimo);
        p.setCategoria(categoria());
        return p;
    }

    private ProductoRequest request() {
        return new ProductoRequest("P-001", "Arroz 1kg", new BigDecimal("2500.00"), 20, 5, 1L, null);
    }

    @Test
    @DisplayName("Crear guarda el producto cuando el código no existe")
    void crear_codigoNuevo_guardaProducto() {
        when(productoRepository.existsByCodigo("P-001")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria()));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponse respuesta = service.crear(request());

        assertEquals("P-001", respuesta.codigo());
        assertEquals("Granos", respuesta.categoria());
        assertEquals(20, respuesta.stock());
    }

    @Test
    @DisplayName("Crear con código repetido lanza RecursoDuplicadoException y no guarda")
    void crear_codigoDuplicado_lanzaExcepcion() {
        when(productoRepository.existsByCodigo("P-001")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> service.crear(request()));

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Crear con una categoría inexistente lanza RecursoNoEncontradoException")
    void crear_categoriaInexistente_lanzaExcepcion() {
        when(productoRepository.existsByCodigo("P-001")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.crear(request()));
    }

    @Test
    @DisplayName("Obtener un id inexistente lanza RecursoNoEncontradoException")
    void obtener_idInexistente_lanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtener(99L));
    }

    @Test
    @DisplayName("Reponer suma la cantidad al stock existente")
    void reponer_cantidadValida_incrementaStock() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto(5, 2)));

        ProductoResponse respuesta = service.reponer(1L, 10);

        assertEquals(15, respuesta.stock());
    }

    @Test
    @DisplayName("Desactivar marca el producto como inactivo en lugar de borrarlo")
    void desactivar_marcaInactivo() {
        Producto p = producto(5, 2);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));

        service.desactivar(1L);

        assertFalse(p.isActivo());
        verify(productoRepository, never()).delete(any(Producto.class));
    }

    @Test
    @DisplayName("Listar con stock bajo marca la bandera stockBajo en la respuesta")
    void listarConStockBajo_marcaBandera() {
        when(productoRepository.findConStockBajo()).thenReturn(List.of(producto(2, 5)));

        List<ProductoResponse> respuesta = service.listarConStockBajo();

        assertEquals(1, respuesta.size());
        assertTrue(respuesta.get(0).stockBajo());
    }
}
