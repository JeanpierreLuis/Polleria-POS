package com.restaurantPos.repositories;

import com.restaurantPos.models.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidosRepository  extends JpaRepository<Pedidos, Long> {
}
