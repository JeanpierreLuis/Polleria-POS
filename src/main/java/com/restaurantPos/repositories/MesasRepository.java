package com.restaurantPos.repositories;

import com.restaurantPos.models.Mesas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MesasRepository  extends JpaRepository<Mesas, Long> {
}
