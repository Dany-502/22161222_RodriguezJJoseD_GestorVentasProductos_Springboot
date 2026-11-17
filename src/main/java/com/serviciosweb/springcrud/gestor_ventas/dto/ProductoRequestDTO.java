package com.serviciosweb.springcrud.gestor_ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductoRequestDTO(
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 5, max = 100, message = "El nombre debe tener entre 5 y 100 caracteres")
    @Schema(example = "Mouse Inalambrico")
    String nombre,

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 enteros y 2 decimales")
    @Schema(example = "1250.50")
    BigDecimal precio,

    @Size (max = 500, message = "La descripcion debe tener menos de 500 caracteres")
    @Schema(example = "Mouse de tipo inalambrico recargable y ergonomico")
    String descripcion,

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Schema(example = "5")
    Integer stock

){}

