package com.inventienda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.inventienda.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    boolean existsByCategoriaId(Long categoriaId);

    boolean existsByProveedorId(Long proveedorId);

    List<Producto> findByActivoTrueOrderByNombreAsc();

    @Query("select p from Producto p where p.activo = true and p.stock <= p.stockMinimo order by p.stock asc")
    List<Producto> findConStockBajo();
}
