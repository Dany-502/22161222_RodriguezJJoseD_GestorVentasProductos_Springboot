package com.serviciosweb.springcrud.gestor_ventas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.serviciosweb.springcrud.gestor_ventas.dto.ProductoRequestDTO;
import com.serviciosweb.springcrud.gestor_ventas.dto.ProductoResponseDTO;
import com.serviciosweb.springcrud.gestor_ventas.exception.ResourceNotAvailable;
import com.serviciosweb.springcrud.gestor_ventas.model.Producto;
import com.serviciosweb.springcrud.gestor_ventas.repository.ProductoRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> findAll() {
        return productoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO findById(Long id) {
        Producto producto = buscarEntidadPorId(id);
        return toResponseDTO(producto);
    }

    @Transactional
    public ProductoResponseDTO saveProducto(ProductoRequestDTO requestDTO) {
        Producto nuevoProducto = toEntity(requestDTO);
        Producto productoGuardado = productoRepository.save(nuevoProducto);
        return toResponseDTO(productoGuardado);
    }

    @Transactional
    public ProductoResponseDTO updateProducto(Long id, ProductoRequestDTO requestDTO) {
        Producto productoExistente = buscarEntidadPorId(id);

        productoExistente.setNombre(requestDTO.nombre());
        productoExistente.setPrecio(requestDTO.precio());
        productoExistente.setDescripcion(requestDTO.descripcion());
        productoExistente.setStock(requestDTO.stock());

        Producto productoActualizado = productoRepository.save(productoExistente);
        return toResponseDTO(productoActualizado);
    }

    @Transactional
    public void deleteProducto(Long id) {
        Producto productoExistente = buscarEntidadPorId(id);
        productoRepository.delete(productoExistente);
    }

    // Metodos auxiliares
    private Producto buscarEntidadPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotAvailable(
                        "No se encontró el producto con ID: " + id));
    }

    private Producto toEntity(ProductoRequestDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.nombre());
        producto.setPrecio(dto.precio());
        producto.setDescripcion(dto.descripcion());
        producto.setStock(dto.stock());
        return producto;
    }

    private ProductoResponseDTO toResponseDTO(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock());
    }
}
