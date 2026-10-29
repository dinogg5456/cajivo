package com.inventienda.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventienda.dto.ClienteRequest;
import com.inventienda.dto.ClienteResponse;
import com.inventienda.exception.RecursoDuplicadoException;
import com.inventienda.exception.RecursoNoEncontradoException;
import com.inventienda.exception.ReglaNegocioException;
import com.inventienda.model.Cliente;
import com.inventienda.repository.ClienteRepository;
import com.inventienda.repository.VentaRepository;
import com.inventienda.service.ClienteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;

    @Override
    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        if (clienteRepository.existsByDocumento(request.documento())) {
            throw new RecursoDuplicadoException("Ya existe un cliente con el documento " + request.documento());
        }
        Cliente cliente = new Cliente();
        aplicar(cliente, request);
        return ClienteResponse.from(clienteRepository.save(cliente));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream().map(ClienteResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtener(Long id) {
        return ClienteResponse.from(buscar(id));
    }

    @Override
    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscar(id);
        if (clienteRepository.existsByDocumentoAndIdNot(request.documento(), id)) {
            throw new RecursoDuplicadoException("Ya existe un cliente con el documento " + request.documento());
        }
        aplicar(cliente, request);
        return ClienteResponse.from(cliente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = buscar(id);
        if (ventaRepository.existsByClienteId(id)) {
            throw new ReglaNegocioException("No se puede eliminar un cliente que tiene ventas registradas");
        }
        clienteRepository.delete(cliente);
    }

    private Cliente buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", id));
    }

    private void aplicar(Cliente cliente, ClienteRequest request) {
        cliente.setDocumento(request.documento().trim());
        cliente.setNombre(request.nombre().trim());
        cliente.setTelefono(request.telefono());
    }
}
