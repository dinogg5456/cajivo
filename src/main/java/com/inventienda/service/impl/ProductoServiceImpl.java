package com.inventienda.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventienda.dto.ProductoRequest;
import com.inventienda.dto.ProductoResponse;
import com.inventienda.exception.RecursoDuplicadoException;
import com.inventienda.exception.RecursoNoEncontradoException;
import com.inventienda.model.Categoria;
import com.inventienda.model.Producto;
import com.inventienda.model.Proveedor;
import com.inventienda.repository.CategoriaRepository;
import com.inventienda.repository.ProductoRepository;
import com.inventienda.repository.ProveedorRepository;
import com.inventienda.service.ProductoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        if (productoRepository.existsByCodigo(request.codigo())) {
            throw new RecursoDuplicadoException("Ya existe un producto con el código " + request.codigo());
        }
        Producto producto = new Producto();
        aplicar(producto, request);
        return ProductoResponse.from(productoRepository.save(producto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarActivos() {
        return productoRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(ProductoResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarConStockBajo() {
        return productoRepository.findConStockBajo().stream()
                .map(ProductoResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return ProductoResponse.from(buscar(id));
    }

    @Override
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscar(id);
        if (productoRepository.existsByCodigoAndIdNot(request.codigo(), id)) {
            throw new RecursoDuplicadoException("Ya existe un producto con el código " + request.codigo());
        }
        aplicar(producto, request);
        return ProductoResponse.from(producto);
    }

    @Override
    @Transactional
    public ProductoResponse reponer(Long id, int cantidad) {
        Producto producto = buscar(id);
        producto.reponerStock(cantidad);
        return ProductoResponse.from(producto);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        buscar(id).setActivo(false);
    }

    private Producto buscar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
    }

    private void aplicar(Producto producto, ProductoRequest request) {
        producto.setCodigo(request.codigo().trim());
        producto.setNombre(request.nombre().trim());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setStockMinimo(request.stockMinimo());
        producto.setCategoria(buscarCategoria(request.categoriaId()));
        producto.setProveedor(request.proveedorId() == null ? null : buscarProveedor(request.proveedorId()));
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría", id));
    }

    private Proveedor buscarProveedor(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor", id));
    }
}
