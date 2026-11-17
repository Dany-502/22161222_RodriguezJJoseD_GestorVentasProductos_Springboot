package com.serviciosweb.springcrud.gestor_ventas.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * DTO para la respuesta de una venta completa (ticket/resumen).
 * Muestra el ID de venta, la fecha, el total acumulado y el desglose de productos.
 */
public record VentaResponseDTO(
    @Schema(description = "Identificador único de la venta", example = "1")
    Long id,

    @Schema(description = "Fecha en que se registró la venta", example = "2026-11-17")
    Date fechaVenta,

    @Schema(description = "Monto total de la venta (suma de todos los subtotales)", example = "3501.00")
    BigDecimal total,

    @ArraySchema(schema = @Schema(description = "Lista detallada de productos comprados"))
    List<DetalleVentaResponseDTO> items
) {}
