package com.restaurantPos.repositories;

import com.restaurantPos.models.Usuarios;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuarios, Long> {
    Optional<Usuarios> findByUsername(String username);
}
