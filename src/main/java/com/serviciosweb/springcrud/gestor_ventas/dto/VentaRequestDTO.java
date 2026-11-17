package com.serviciosweb.springcrud.gestor_ventas.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO para la petición de registrar o actualizar una venta.
 * Recibe una lista de items, cada uno con su producto y cantidad específica.
 */
public record VentaRequestDTO(
    @NotEmpty(message = "Se requiere al menos un producto en la venta")
    @ArraySchema(schema = @Schema(description = "Lista de productos y sus cantidades a comprar"))
    List<@Valid DetalleVentaRequestDTO> items
) {}
