package com.inventienda.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventiendaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("InvenTienda API")
                .version("1.0.0")
                .description("API REST para controlar inventario, proveedores, clientes y ventas de un minimercado de barrio."));
    }
}
