# Microservicio · Gestión de Inventario de Productos y Ventas

Microservicio RESTful desarrollado con **Spring Boot** y **PostgreSQL** para la administración de un catálogo de productos y el registro de ventas, permitiendo controlar el stock en tiempo real y consultar el historial detallado de transacciones.

## Autor

* **Nombre:** Rodríguez Juárez José Daniel
* **Número de control:** 22161222
* **Institución:** Instituto Tecnológico de Oaxaca
* **Materia:** Desarrollo de Servicios Web

## Características

* **Arquitectura por Capas:** Separación clara de responsabilidades mediante paquetes `controller`, `service`, `repository`, `model`, `dto` y `exception`.
* **Gestión de Productos (`/api/productos`):**
  * Listado completo del catálogo de productos con ID, nombre, precio y stock disponible.
  * Consulta individual de productos por su identificador único (`ID`).
  * Registro y actualización de productos con validaciones de entrada (`@NotBlank`, `@Positive`, `@PositiveOrZero`, `@Digits`, `@Size`).
  * Eliminación de productos del catálogo.
* **Gestión de Ventas Maestro-Detalle (`/api/ventas`):**
  * Registro de ventas con soporte para múltiples productos y cantidades individuales por cada artículo.
  * **Control automático de inventario:**
    * Validación de stock disponible antes de procesar la venta.
    * Descuento automático de unidades al confirmar una venta.
    * Devolución y reajuste automático de stock al editar o eliminar una venta.
  * Cálculo automático de precios unitarios históricos, subtotales por renglón y monto total de la venta.
* **Manejo Global de Excepciones (`@RestControllerAdvice`):**
  * Respuestas estandarizadas en formato JSON para errores de validación (`400 Bad Request`), stock insuficiente (`400 Bad Request`), recursos inexistentes (`404 Not Found`) y errores internos (`500 Internal Server Error`).
* **Documentación Interactiva (OpenAPI / Swagger UI):**
  * Interfaz visual generada con `springdoc-openapi` para probar todos los endpoints directamente desde el navegador.

## Estructura de archivos

```text
22161222_RodriguezJJoseD_BackSpring/
├── src/
│   ├── main/
│   │   ├── java/com/serviciosweb/springcrud/gestor_ventas/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   │   ├── ProductoController.java       # Endpoints REST para productos (/api/productos)
│   │   │   │   └── VentaController.java          # Endpoints REST para ventas (/api/ventas)
│   │   │   ├── dto/
│   │   │   │   ├── DetalleVentaRequestDTO.java   # Datos de entrada por renglón (idProducto, cantidad)
│   │   │   │   ├── DetalleVentaResponseDTO.java  # Desglose de cada producto vendido en el ticket
│   │   │   │   ├── ProductoRequestDTO.java       # Datos de entrada y validaciones de producto
│   │   │   │   ├── ProductoResponseDTO.java      # Datos de salida de producto
│   │   │   │   ├── VentaRequestDTO.java          # Datos de entrada de una venta (lista de items)
│   │   │   │   └── VentaResponseDTO.java         # Datos de salida de la venta (total y desglose)
│   │   │   ├── exception/
│   │   │   │   ├── ErrorResponse.java            # Estructura estándar de errores JSON
│   │   │   │   ├── GlobalExceptionHandler.java   # Manejador global de excepciones HTTP
│   │   │   │   └── ResourceNotAvailable.java     # Excepción personalizada para recursos no encontrados (404)
│   │   │   ├── model/
│   │   │   │   ├── DetalleVenta.java             # Entidad JPA para los renglones de cada venta
│   │   │   │   ├── Producto.java                 # Entidad JPA para el catálogo de productos
│   │   │   │   └── Venta.java                    # Entidad JPA maestro de la venta
│   │   │   ├── repository/
│   │   │   │   ├── ProductoRepository.java       # Repositorio JPA para la tabla productos
│   │   │   │   └── VentaRepository.java          # Repositorio JPA para la tabla ventas
│   │   │   ├── service/
│   │   │   │   ├── ProductoService.java          # Lógica de negocio del módulo de productos
│   │   │   │   └── VentaService.java             # Lógica de negocio, cálculo de totales y control de stock
│   │   │   └── GestorVentasApplication.java      # Clase principal de arranque de Spring Boot
│   │   └── resources/
│   │       └── application.yaml                  # Configuración de puerto, PostgreSQL y JPA/Hibernate
│   └── test/
└── pom.xml                                       # Dependencias y configuración de Maven
```

## Tablas creadas para la base de datos

El microservicio utiliza **Hibernate (`ddl-auto: update`)** sobre la base de datos **`inventario`** en PostgreSQL, generando automáticamente las siguientes 3 tablas relacionadas:

### 1. Tabla `productos`
Almacena el catálogo e inventario de productos disponibles.

| Columna | Tipo de dato | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY`, Autoincremental | Identificador único del producto |
| `nombre` | `VARCHAR(100)` | `NOT NULL` | Nombre del producto |
| `precio` | `NUMERIC(10,2)` | `NOT NULL` | Precio unitario actual |
| `descripcion` | `VARCHAR(500)` | `NOT NULL` | Descripción detallada del producto |
| `stock` | `INTEGER` | — | Cantidad de unidades disponibles en inventario |

### 2. Tabla `ventas`
Tabla maestro que registra el encabezado de cada transacción de venta.

| Columna | Tipo de dato | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY`, Autoincremental | Identificador único de la venta |
| `fecha_venta` | `DATE` | `NOT NULL` | Fecha en que se realizó la venta |
| `total` | `NUMERIC(10,2)` | `NOT NULL` | Monto total acumulado de la venta |

### 3. Tabla `detalles_venta`
Tabla detalle que desglosa los productos adquiridos, sus cantidades y subtotales por cada venta.

| Columna | Tipo de dato | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY`, Autoincremental | Identificador único del renglón de detalle |
| `venta_id` | `BIGINT` | `FOREIGN KEY -> ventas(id)`, `NOT NULL` | Venta a la que pertenece el renglón |
| `producto_id` | `BIGINT` | `FOREIGN KEY -> productos(id)`, `NOT NULL` | Producto vendido |
| `cantidad` | `INTEGER` | `NOT NULL` | Cantidad de unidades vendidas de ese producto |
| `precio_unitario` | `NUMERIC(10,2)` | `NOT NULL` | Precio unitario del producto al momento de la venta |
| `subtotal` | `NUMERIC(10,2)` | `NOT NULL` | Subtotal del renglón (`precio_unitario * cantidad`) |

## Instalación y uso

### Requisitos previos
* **Java JDK 17** o superior.
* **PostgreSQL 16** (corriendo en el puerto `5432`).
* **Maven** (incluido mediante el wrapper `mvnw`).

### 1. Crear la base de datos en PostgreSQL
Ejecuta el siguiente comando en la terminal o desde **pgAdmin 4** para crear la base de datos vacía (Hibernate creará las tablas automáticamente al iniciar):

```powershell
psql -U postgres -c "CREATE DATABASE inventario;"
```

### 2. Configurar credenciales
Verifica los datos de conexión a PostgreSQL en el archivo `src/main/resources/application.yaml`:

```yaml
server:
  port: 8000

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/inventario
    username: postgres
    password: tu_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
```

### 3. Ejecutar el microservicio
Desde la raíz del proyecto, levanta el servidor ejecutando:

```powershell
.\mvnw.cmd spring-boot:run
```

### 4. Probar los endpoints
Una vez iniciado el servidor en el puerto `8000`, accede a la documentación interactiva en Swagger UI:

* **Swagger UI:** `http://localhost:8000/swagger-ui/index.html`
* **API Productos:** `http://localhost:8000/api/productos`
* **API Ventas:** `http://localhost:8000/api/ventas`
