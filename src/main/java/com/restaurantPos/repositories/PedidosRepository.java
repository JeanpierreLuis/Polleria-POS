package com.restaurantPos.repositories;

import com.restaurantPos.models.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidosRepository  extends JpaRepository<Pedidos, Long> {

    @Query("""
        SELECT DISTINCT p FROM Pedidos p 
        LEFT JOIN FETCH p.detalles d
        LEFT JOIN FETCH d.producto
        WHERE p.estado = :estado
""")
    List<Pedidos> findByEstadoConDetalles(@Param("estado")Pedidos.EstadoPedido estado);
}
