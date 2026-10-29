package com.inventienda.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventienda.dto.ProductoRequest;
import com.inventienda.dto.ProductoResponse;
import com.inventienda.exception.RecursoNoEncontradoException;
import com.inventienda.service.ProductoService;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    private ProductoResponse respuesta() {
        return new ProductoResponse(1L, "P-001", "Arroz 1kg", new BigDecimal("2500.00"),
                20, 5, true, false, "Granos", null);
    }

    @Test
    @DisplayName("POST con datos válidos responde 201 y el producto creado")
    void crear_datosValidos_responde201() throws Exception {
        ProductoRequest request = new ProductoRequest("P-001", "Arroz 1kg",
                new BigDecimal("2500.00"), 20, 5, 1L, null);
        when(productoService.crear(any(ProductoRequest.class))).thenReturn(respuesta());

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("P-001"))
                .andExpect(jsonPath("$.categoria").value("Granos"));
    }

    @Test
    @DisplayName("POST con precio cero responde 400 y detalla el campo inválido")
    void crear_precioInvalido_responde400() throws Exception {
        ProductoRequest request = new ProductoRequest("P-001", "Arroz 1kg",
                new BigDecimal("0.00"), 20, 5, 1L, null);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles").isNotEmpty());
    }

    @Test
    @DisplayName("GET de un producto inexistente responde 404 con el mensaje del servicio")
    void obtener_inexistente_responde404() throws Exception {
        when(productoService.obtener(99L)).thenThrow(new RecursoNoEncontradoException("Producto", 99L));

        mockMvc.perform(get("/api/v1/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Producto con id 99 no fue encontrado"));
    }

    @Test
    @DisplayName("GET del listado responde 200 con un arreglo JSON")
    void listar_responde200ConArreglo() throws Exception {
        when(productoService.listarActivos()).thenReturn(List.of(respuesta()));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Arroz 1kg"));
    }
}
