package com.restaurantPos.repositories;

import com.restaurantPos.models.Productos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductosRepository extends JpaRepository<Productos, Long> {
}
