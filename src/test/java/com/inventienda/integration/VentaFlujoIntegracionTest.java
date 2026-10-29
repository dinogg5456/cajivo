package com.inventienda.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventienda.dto.CategoriaRequest;
import com.inventienda.dto.ItemVentaRequest;
import com.inventienda.dto.ProductoRequest;
import com.inventienda.dto.VentaRequest;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VentaFlujoIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Vender descuenta el stock y anular lo devuelve, con base de datos real en memoria")
    void vender_y_anular_actualizanElInventario() throws Exception {
        long categoriaId = crearCategoria("Granos-" + System.nanoTime());
        long productoId = crearProducto("INT-001", "Arroz 1kg", "2500.00", 10, categoriaId);

        String ventaJson = mockMvc.perform(post("/api/v1/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new VentaRequest(null, List.of(new ItemVentaRequest(productoId, 4))))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(10000.0))
                .andReturn().getResponse().getContentAsString();
        long ventaId = leerId(ventaJson);

        mockMvc.perform(get("/api/v1/productos/" + productoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(6));

        mockMvc.perform(patch("/api/v1/ventas/" + ventaId + "/anular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ANULADA"));

        mockMvc.perform(get("/api/v1/productos/" + productoId))
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    @DisplayName("Una venta que supera el stock responde 422 y deja el inventario intacto")
    void vender_sinStockSuficiente_responde422() throws Exception {
        long categoriaId = crearCategoria("Lacteos-" + System.nanoTime());
        long productoId = crearProducto("INT-002", "Leche 1L", "3200.00", 1, categoriaId);

        mockMvc.perform(post("/api/v1/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new VentaRequest(null, List.of(new ItemVentaRequest(productoId, 5))))))
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(get("/api/v1/productos/" + productoId))
                .andExpect(jsonPath("$.stock").value(1));
    }

    private long crearCategoria(String nombre) throws Exception {
        String cuerpo = mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CategoriaRequest(nombre, "Categoría de prueba"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return leerId(cuerpo);
    }

    private long crearProducto(String codigo, String nombre, String precio, int stock, long categoriaId)
            throws Exception {
        ProductoRequest request = new ProductoRequest(codigo, nombre, new BigDecimal(precio),
                stock, 2, categoriaId, null);
        String cuerpo = mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return leerId(cuerpo);
    }

    private String json(Object objeto) throws Exception {
        return objectMapper.writeValueAsString(objeto);
    }

    private long leerId(String cuerpo) {
        Number id = JsonPath.read(cuerpo, "$.id");
        return id.longValue();
    }
}
