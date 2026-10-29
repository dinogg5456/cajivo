package com.inventienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventienda.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByDocumento(String documento);

    boolean existsByDocumentoAndIdNot(String documento, Long id);
}
