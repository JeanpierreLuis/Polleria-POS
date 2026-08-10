package com.restaurantPos.repositories;

import com.restaurantPos.models.Caja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CajaRepository extends JpaRepository<Caja, Long> {
    Optional<Caja> findFirstByEstadoIgnoreCaseOrderByFechaAperturaDesc(String estado);
}
