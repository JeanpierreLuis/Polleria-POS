package com.restaurantPos.repositories;

import com.restaurantPos.models.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientesRepository extends JpaRepository<Clientes, Long> {
}
