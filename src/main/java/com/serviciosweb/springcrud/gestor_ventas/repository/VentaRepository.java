package com.serviciosweb.springcrud.gestor_ventas.repository;

import com.serviciosweb.springcrud.gestor_ventas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByDetallesProductoId(Long productoId);
}
