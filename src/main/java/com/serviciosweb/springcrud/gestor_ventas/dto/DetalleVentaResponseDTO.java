package com.serviciosweb.springcrud.gestor_ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * DTO para devolver el desglose de cada producto vendido en la respuesta (ticket).
 */
public record DetalleVentaResponseDTO(
    @Schema(description = "ID del renglón de detalle", example = "1")
    Long id,

    @Schema(description = "ID del producto adquirido", example = "1")
    Long idProducto,

    @Schema(description = "Nombre del producto adquirido", example = "Teclado Mecánico RGB")
    String nombreProducto,

    @Schema(description = "Precio unitario del producto al momento de la venta", example = "1250.50")
    BigDecimal precioUnitario,

    @Schema(description = "Cantidad de unidades adquiridas", example = "2")
    Integer cantidad,

    @Schema(description = "Subtotal de este producto (precioUnitario * cantidad)", example = "2501.00")
    BigDecimal subtotal
) {}
