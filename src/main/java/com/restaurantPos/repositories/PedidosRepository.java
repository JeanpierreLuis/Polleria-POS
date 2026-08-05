package com.restaurantPos.repositories;

import com.restaurantPos.models.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidosRepository  extends JpaRepository<Pedidos, Long> {

    @Query("""
        SELECT DISTINCT p FROM Pedidos p 
        LEFT JOIN FETCH p.detalles d
        LEFT JOIN FETCH d.producto
        WHERE p.estado IN :estados
        and p.fecha BETWEEN :inicioDia and :finDia
""")
    List<Pedidos> findByEstadoConDetalles(
            @Param("estados") List<Pedidos.EstadoPedido> estados,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDia") LocalDateTime finDia);

    @Query("""
    SELECT COUNT(p)
    FROM Pedidos p
    WHERE p.estado = :estado
    AND p.fecha BETWEEN :inicioDia AND :finDia
""")
    Long countByEstadoAndFecha(
            @Param("estado") Pedidos.EstadoPedido estado,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDia") LocalDateTime finDia
    );
}
