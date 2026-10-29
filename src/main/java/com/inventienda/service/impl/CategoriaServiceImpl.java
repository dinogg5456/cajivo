package com.inventienda.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventienda.dto.CategoriaRequest;
import com.inventienda.dto.CategoriaResponse;
import com.inventienda.exception.RecursoDuplicadoException;
import com.inventienda.exception.RecursoNoEncontradoException;
import com.inventienda.exception.ReglaNegocioException;
import com.inventienda.model.Categoria;
import com.inventienda.repository.CategoriaRepository;
import com.inventienda.repository.ProductoRepository;
import com.inventienda.service.CategoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new RecursoDuplicadoException("Ya existe una categoría llamada " + request.nombre());
        }
        Categoria categoria = new Categoria();
        aplicar(categoria, request);
        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(CategoriaResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Long id) {
        return CategoriaResponse.from(buscar(id));
    }

    @Override
    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscar(id);
        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(request.nombre(), id)) {
            throw new RecursoDuplicadoException("Ya existe una categoría llamada " + request.nombre());
        }
        aplicar(categoria, request);
        return CategoriaResponse.from(categoria);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscar(id);
        if (productoRepository.existsByCategoriaId(id)) {
            throw new ReglaNegocioException("No se puede eliminar una categoría que tiene productos asociados");
        }
        categoriaRepository.delete(categoria);
    }

    private Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría", id));
    }

    private void aplicar(Categoria categoria, CategoriaRequest request) {
        categoria.setNombre(request.nombre().trim());
        categoria.setDescripcion(request.descripcion());
    }
}
