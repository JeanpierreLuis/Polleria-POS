package com.restaurantPos.repositories;

import com.restaurantPos.models.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuarios, Long> {
    Optional<Usuarios> findByUsername(String nombre);
}
