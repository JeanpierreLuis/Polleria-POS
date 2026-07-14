package com.restaurantPos.repositories;

import com.restaurantPos.models.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuarios, Long> {
    Optional<Usuarios> findByUsername(String nombre);
}
