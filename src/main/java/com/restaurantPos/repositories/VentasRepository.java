package com.restaurantPos.repositories;

import com.restaurantPos.models.Ventas;
import com.restaurantPos.models.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VentasRepository extends JpaRepository<Ventas, Long> {
    boolean existsByPedidoAndEstadopago(Pedidos pedido, Ventas.EstadoPago estadopago);

    Optional<Ventas> findByPedidoAndEstadopago(Pedidos pedido, Ventas.EstadoPago estadoPago);
}
