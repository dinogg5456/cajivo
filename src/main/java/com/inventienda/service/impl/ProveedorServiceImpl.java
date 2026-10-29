package com.inventienda.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventienda.dto.ProveedorRequest;
import com.inventienda.dto.ProveedorResponse;
import com.inventienda.exception.RecursoNoEncontradoException;
import com.inventienda.exception.ReglaNegocioException;
import com.inventienda.model.Proveedor;
import com.inventienda.repository.ProductoRepository;
import com.inventienda.repository.ProveedorRepository;
import com.inventienda.service.ProveedorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public ProveedorResponse crear(ProveedorRequest request) {
        Proveedor proveedor = new Proveedor();
        aplicar(proveedor, request);
        return ProveedorResponse.from(proveedorRepository.save(proveedor));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponse> listar() {
        return proveedorRepository.findAll().stream().map(ProveedorResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse obtener(Long id) {
        return ProveedorResponse.from(buscar(id));
    }

    @Override
    @Transactional
    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {
        Proveedor proveedor = buscar(id);
        aplicar(proveedor, request);
        return ProveedorResponse.from(proveedor);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Proveedor proveedor = buscar(id);
        if (productoRepository.existsByProveedorId(id)) {
            throw new ReglaNegocioException("No se puede eliminar un proveedor que tiene productos asociados");
        }
        proveedorRepository.delete(proveedor);
    }

    private Proveedor buscar(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor", id));
    }

    private void aplicar(Proveedor proveedor, ProveedorRequest request) {
        proveedor.setNombre(request.nombre().trim());
        proveedor.setTelefono(request.telefono());
        proveedor.setEmail(request.email());
    }
}
