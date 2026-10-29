package com.inventienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventienda.model.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
}
