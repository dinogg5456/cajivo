package com.inventienda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventienda.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findAllByOrderByFechaDesc();

    boolean existsByClienteId(Long clienteId);
}
