package com.restaurantPos.repositories;

import com.restaurantPos.models.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientesRepository extends JpaRepository<Clientes, Long> {
    Optional<Clientes> findByNombre(String nombre);
}

