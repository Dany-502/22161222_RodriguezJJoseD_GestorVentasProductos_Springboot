package com.serviciosweb.springcrud.gestor_ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para recibir cada producto individual y su cantidad dentro de una venta.
 */
public record DetalleVentaRequestDTO(
    @NotNull(message = "El id del producto es obligatorio")
    @Positive(message = "El id del producto debe ser un número positivo")
    @Schema(description = "Identificador único del producto a comprar", example = "1")
    Long idProducto,

    @NotNull(message = "La cantidad del producto es obligatoria")
    @Positive(message = "La cantidad debe ser al menos 1")
    @Schema(description = "Cantidad de unidades que se desean adquirir de este producto", example = "2")
    Integer cantidad
) {}
