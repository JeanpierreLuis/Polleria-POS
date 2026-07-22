package com.restaurantPos.repositories;

import com.restaurantPos.models.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categorias, Long> {
}
