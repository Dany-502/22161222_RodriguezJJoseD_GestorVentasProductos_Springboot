package com.serviciosweb.springcrud.gestor_ventas.model;

import jakarta.persistence.*; // 
import java.math.BigDecimal; // 

@Entity
@Table(name = "productos")
public class Producto {

    // Llave primaria autogenerada
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del producto
    @Column(nullable = false, length = 100)
    private String nombre;

    // Precio del producto
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    // Descripcion del producto
    @Column(nullable = false, length = 500)
    private String descripcion;

    // Stock del producto
    private Integer stock;

    public Producto() {
    }

    // GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

}
