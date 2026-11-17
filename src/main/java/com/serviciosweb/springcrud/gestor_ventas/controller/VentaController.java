package com.serviciosweb.springcrud.gestor_ventas.controller;

import com.serviciosweb.springcrud.gestor_ventas.dto.VentaRequestDTO;
import com.serviciosweb.springcrud.gestor_ventas.dto.VentaResponseDTO;
import com.serviciosweb.springcrud.gestor_ventas.exception.ErrorResponse;
import com.serviciosweb.springcrud.gestor_ventas.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Operaciones CRUD para la gestión del registro de ventas")
public class VentaController {
    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // ENDPOINT PARA LISTAR TODAS LAS VENTAS
    @Operation(summary = "Listar todas las ventas", description = "Obtiene la lista de ventas registradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ventas recuperada con éxito", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = VentaResponseDTO.class))))
    })
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listVentas() {
        return ResponseEntity.ok(ventaService.findAll());
    }

    // OBTENER UNA VENTA POR SU ID
    @Operation(summary = "Obtener venta por ID", description = "Busca y devuelve el detalle de una venta específica a partir de su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta encontrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VentaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe una venta con el ID proporcionado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> obtenerVentaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.findById(id));
    }

    // ENDPOINT PARA REGISTRAR UNA NUEVA VENTA
    @Operation(summary = "Registrar una nueva venta", description = "Valida los datos, descuenta el stock de los productos y guarda la venta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta registrada exitosamente", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VentaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o stock insuficiente", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<VentaResponseDTO> crearVenta(@Valid @RequestBody VentaRequestDTO venta) {
        VentaResponseDTO nuevaVenta = ventaService.saveVenta(venta);
        return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
    }

    // ENDPOINT PARA ACTUALIZAR UNA VENTA
    @Operation(summary = "Actualizar una venta existente", description = "Actualiza los productos y cantidad de una venta, ajustando el stock y subtotal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta actualizada exitosamente", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VentaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró la venta con el ID indicado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> updateVenta(
            @PathVariable Long id,
            @Valid @RequestBody VentaRequestDTO requestDTO) {
        return ResponseEntity.ok(ventaService.updateVenta(id, requestDTO));
    }

    // ENDPOINT PARA ELIMINAR UNA VENTA
    @Operation(summary = "Eliminar una venta", description = "Elimina una venta por su ID y devuelve el stock a los productos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Venta eliminada correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró la venta a eliminar con el ID indicado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        ventaService.deleteVenta(id);
        return ResponseEntity.noContent().build();
    }
}
