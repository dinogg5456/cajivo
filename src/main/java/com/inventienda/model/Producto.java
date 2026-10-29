package com.inventienda.model;

import java.math.BigDecimal;

import com.inventienda.exception.ReglaNegocioException;
import com.inventienda.exception.StockInsuficienteException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private int stock;

    @Column(name = "stock_minimo", nullable = false)
    private int stockMinimo;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    public void descontarStock(int cantidad) {
        validarCantidad(cantidad);
        if (cantidad > stock) {
            throw new StockInsuficienteException(nombre, stock, cantidad);
        }
        stock -= cantidad;
    }

    public void reponerStock(int cantidad) {
        validarCantidad(cantidad);
        stock += cantidad;
    }

    public boolean tieneStockBajo() {
        return stock <= stockMinimo;
    }

    private void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
        }
    }
}
