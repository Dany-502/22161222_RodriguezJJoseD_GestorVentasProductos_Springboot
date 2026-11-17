package com.serviciosweb.springcrud.gestor_ventas.repository;

import com.serviciosweb.springcrud.gestor_ventas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto,Long> {
}
