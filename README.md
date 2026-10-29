# InvenTienda API

API REST para controlar el inventario, los proveedores, los clientes y las ventas de un minimercado de barrio.
Proyecto del curso Ingeniería de Software (trabajo colaborativo contextualizado).

**Stack:** Java 17, Spring Boot 3.3.5, Spring Data JPA, PostgreSQL 16, Maven, JUnit 5, Mockito, Docker.

## Requisitos

- Docker y Docker Compose, para ejecutar todo con un comando.
- JDK 17 y Maven 3.9+, si quieres compilar y probar sin Docker.

## Ejecutar con Docker

```bash
docker compose up --build
```

Esto levanta PostgreSQL y la API. Cuando termine de arrancar:

- API: http://localhost:8080/api/v1/productos
- Documentación Swagger: http://localhost:8080/swagger-ui.html
- Salud del servicio: http://localhost:8080/actuator/health

Para apagar y borrar los datos: `docker compose down -v`.

## Ejecutar las pruebas

```bash
mvn test
```

Las pruebas usan H2 en memoria, así que no necesitan PostgreSQL ni Docker.

## Estructura

```
src/main/java/com/inventienda
  controller/   Endpoints REST
  service/      Contratos de la lógica de negocio
  service/impl/ Implementaciones
  repository/   Acceso a datos (Spring Data JPA)
  model/        Entidades JPA y reglas del dominio
  dto/          Objetos de entrada y salida (records con validaciones)
  exception/    Excepciones propias y manejador global
```

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| POST / GET | `/api/v1/categorias` | Crear y listar categorías |
| POST / GET | `/api/v1/proveedores` | Crear y listar proveedores |
| POST / GET | `/api/v1/clientes` | Crear y listar clientes |
| POST / GET | `/api/v1/productos` | Crear y listar productos activos |
| GET | `/api/v1/productos/bajo-stock` | Productos en o por debajo del stock mínimo |
| PATCH | `/api/v1/productos/{id}/reposicion` | Registrar entrada de mercancía |
| DELETE | `/api/v1/productos/{id}` | Desactivar un producto (borrado lógico) |
| POST / GET | `/api/v1/ventas` | Registrar y listar ventas |
| PATCH | `/api/v1/ventas/{id}/anular` | Anular una venta y devolver el stock |

Los demás recursos también tienen `GET /{id}`, `PUT /{id}` y `DELETE /{id}`.

## Pruebas en Postman

Importa `postman/InvenTienda.postman_collection.json` y ejecútala con el Collection Runner.
La variable `baseUrl` apunta a `http://localhost:8080`.

## Reglas de negocio clave

1. El stock nunca puede quedar negativo.
2. Una venta descuenta el stock de cada producto en la misma transacción.
3. El precio unitario se copia a la venta, así que cambiar el precio después no altera ventas pasadas.
4. Una venta anulada devuelve el stock y no se puede anular dos veces.
5. Un producto con ventas no se borra, se desactiva.
6. No se puede eliminar una categoría, un proveedor o un cliente que tenga registros asociados.

## Equipo

Jorge Andrés Flórez García, Eliuth Jiménez Martínez, Luis Fernando Mejía Fonseca y Carlos David Ortiz Aranzalez.
Tutor: Pedro José Consuegra Cañarete.
