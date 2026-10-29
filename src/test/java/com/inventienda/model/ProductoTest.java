package com.inventienda.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.inventienda.exception.ReglaNegocioException;
import com.inventienda.exception.StockInsuficienteException;

class ProductoTest {

    private Producto producto;

    @BeforeEach
    void preparar() {
        producto = new Producto();
        producto.setNombre("Aceite 1L");
        producto.setPrecio(new BigDecimal("9800.00"));
        producto.setStock(10);
        producto.setStockMinimo(3);
    }

    @Test
    @DisplayName("Descontar una cantidad válida reduce el stock")
    void descontarStock_cantidadValida_reduceStock() {
        producto.descontarStock(4);

        assertEquals(6, producto.getStock());
    }

    @Test
    @DisplayName("Descontar más de lo disponible lanza StockInsuficienteException y no cambia el stock")
    void descontarStock_excedeStock_lanzaExcepcion() {
        assertThrows(StockInsuficienteException.class, () -> producto.descontarStock(11));

        assertEquals(10, producto.getStock());
    }

    @Test
    @DisplayName("Reponer cero unidades viola la regla de negocio")
    void reponerStock_cantidadCero_lanzaExcepcion() {
        assertThrows(ReglaNegocioException.class, () -> producto.reponerStock(0));
    }

    @Test
    @DisplayName("El stock igual al mínimo se considera stock bajo")
    void tieneStockBajo_igualAlMinimo_devuelveVerdadero() {
        producto.setStock(3);
        assertTrue(producto.tieneStockBajo());

        producto.setStock(4);
        assertFalse(producto.tieneStockBajo());
    }
}
