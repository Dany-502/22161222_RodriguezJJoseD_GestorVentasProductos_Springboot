package com.serviciosweb.springcrud.gestor_ventas.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviciosweb.springcrud.gestor_ventas.dto.DetalleVentaRequestDTO;
import com.serviciosweb.springcrud.gestor_ventas.dto.DetalleVentaResponseDTO;
import com.serviciosweb.springcrud.gestor_ventas.dto.VentaRequestDTO;
import com.serviciosweb.springcrud.gestor_ventas.dto.VentaResponseDTO;
import com.serviciosweb.springcrud.gestor_ventas.exception.ResourceNotAvailable;
import com.serviciosweb.springcrud.gestor_ventas.model.DetalleVenta;
import com.serviciosweb.springcrud.gestor_ventas.model.Producto;
import com.serviciosweb.springcrud.gestor_ventas.model.Venta;
import com.serviciosweb.springcrud.gestor_ventas.repository.ProductoRepository;
import com.serviciosweb.springcrud.gestor_ventas.repository.VentaRepository;

/**
 * Servicio encargado de la lógica de negocio del módulo de Ventas.
 * Gestiona el control de inventario (validar, descontar y devolver stock),
 * el cálculo de importes (precios unitarios, subtotales y total general)
 * y la persistencia de ventas en estructura Maestro-Detalle.
 */
@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Recupera todas las ventas registradas con el desglose de sus productos.
     */
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> findAll() {
        return ventaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Busca una venta por su identificador único (ID).
     */
    @Transactional(readOnly = true)
    public VentaResponseDTO findById(Long id) {
        Venta venta = buscarVentaPorId(id);
        return toResponseDTO(venta);
    }

    // Registra una nueva venta con múltiples productos y cantidades independientes.
    @Transactional
    public VentaResponseDTO saveVenta(VentaRequestDTO requestDTO) {
        Venta nuevaVenta = new Venta();
        nuevaVenta.setFechaVenta(new Date(System.currentTimeMillis())); // Fecha actual

        BigDecimal totalVenta = BigDecimal.ZERO;

        for (DetalleVentaRequestDTO itemDTO : requestDTO.items()) {
            // Busca producto en BD
            Producto producto = buscarProductoPorId(itemDTO.idProducto());

            // Valida stock disponible
            if (producto.getStock() < itemDTO.cantidad()) {
                throw new IllegalArgumentException(
                        "Stock insuficiente para '" + producto.getNombre() +
                                "'. Disponible: " + producto.getStock() +
                                ", Solicitado: " + itemDTO.cantidad());
            }

            // Descuenta stock del producto
            producto.setStock(producto.getStock() - itemDTO.cantidad());

            // Calcula subtotal del renglón (precioUnitario * cantidad)
            BigDecimal precioUnitario = producto.getPrecio();
            BigDecimal subtotalRenglon = precioUnitario.multiply(BigDecimal.valueOf(itemDTO.cantidad()));

            // Crea el renglón de detalle y asociarlo a la venta
            DetalleVenta detalle = new DetalleVenta(
                    nuevaVenta,
                    producto,
                    itemDTO.cantidad(),
                    precioUnitario,
                    subtotalRenglon);
            nuevaVenta.addDetalle(detalle);

            // Acumula al total de la venta
            totalVenta = totalVenta.add(subtotalRenglon);
        }

        nuevaVenta.setTotal(totalVenta);

        // Al guardar la venta, JPA persiste en cascada (CascadeType.ALL) los detalles
        // asociados
        Venta ventaGuardada = ventaRepository.save(nuevaVenta);
        return toResponseDTO(ventaGuardada);
    }

    // Elimina una venta del sistema y devuelve todo el stock vendido a los
    // productos correspondientes.
    @Transactional
    public void deleteVenta(Long id) {
        Venta ventaExistente = buscarVentaPorId(id);

        // Devuelve el stock a cada producto antes de eliminar el registro
        for (DetalleVenta detalle : ventaExistente.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
        }

        ventaRepository.delete(ventaExistente);
    }

    // ==========================================
    // FUNCIONES AUXILIARES PRIVADAS
    // ==========================================

    // Busca una entidad Venta en base de datos o lanza excepción si no existe.
    private Venta buscarVentaPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotAvailable("Venta no encontrada con ID: " + id));
    }

    // Busca una entidad Producto en base de datos o lanza excepción si no existe.
    private Producto buscarProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotAvailable("Producto no encontrado con ID: " + id));
    }

    // Transforma una entidad Venta completa a su DTO de respuesta con el desglose
    // de productos.
    private VentaResponseDTO toResponseDTO(Venta venta) {
        List<DetalleVentaResponseDTO> itemsDTO = venta.getDetalles()
                .stream()
                .map(this::toDetalleResponseDTO)
                .toList();

        return new VentaResponseDTO(
                venta.getId(),
                venta.getFechaVenta(),
                venta.getTotal(),
                itemsDTO);
    }

    // Transforma un renglón DetalleVenta a su correspondiente
    // DetalleVentaResponseDTO.
    private DetalleVentaResponseDTO toDetalleResponseDTO(DetalleVenta detalle) {
        return new DetalleVentaResponseDTO(
                detalle.getId(),
                detalle.getProducto().getId(),
                detalle.getProducto().getNombre(),
                detalle.getPrecioUnitario(),
                detalle.getCantidad(),
                detalle.getSubtotal());
    }
}

    /*
    // Actualiza una venta existente reemplazando sus productos y cantidades. (Deshabilitado temporalmente)
    @Transactional
    public VentaResponseDTO updateVenta(Long id, VentaRequestDTO requestDTO) {
        Venta ventaExistente = buscarVentaPorId(id);

        // Revierten y devuelven el stock a los productos que estaban en la venta
        // original
        for (DetalleVenta detalleViejo : ventaExistente.getDetalles()) {
            Producto prod = detalleViejo.getProducto();
            prod.setStock(prod.getStock() + detalleViejo.getCantidad());
        }

        // Limpia los renglones anteriores (orphanRemoval los eliminará en BD)
        ventaExistente.getDetalles().clear();

        // Procesa los nuevos items de la venta
        BigDecimal nuevoTotal = BigDecimal.ZERO;

        for (DetalleVentaRequestDTO itemDTO : requestDTO.items()) {
            Producto producto = buscarProductoPorId(itemDTO.idProducto());

            // Valida stock disponible
            if (producto.getStock() < itemDTO.cantidad()) {
                throw new IllegalArgumentException(
                        "Stock insuficiente para '" + producto.getNombre() +
                                "'. Disponible: " + producto.getStock() +
                                ", Solicitado: " + itemDTO.cantidad());
            }

            // Descuenta nuevo stock
            producto.setStock(producto.getStock() - itemDTO.cantidad());

            BigDecimal precioUnitario = producto.getPrecio();
            BigDecimal subtotalRenglon = precioUnitario.multiply(BigDecimal.valueOf(itemDTO.cantidad()));

            DetalleVenta nuevoDetalle = new DetalleVenta(
                    ventaExistente,
                    producto,
                    itemDTO.cantidad(),
                    precioUnitario,
                    subtotalRenglon);
            ventaExistente.addDetalle(nuevoDetalle);

            nuevoTotal = nuevoTotal.add(subtotalRenglon);
        }

        ventaExistente.setTotal(nuevoTotal);

        Venta ventaActualizada = ventaRepository.save(ventaExistente);
        return toResponseDTO(ventaActualizada);
    }
    */
