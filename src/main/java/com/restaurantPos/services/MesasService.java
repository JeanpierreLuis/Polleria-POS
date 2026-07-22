package com.restaurantPos.services;

import com.restaurantPos.models.Mesas;
import com.restaurantPos.repositories.MesasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MesasService {
    private final MesasRepository mesasRepository;

    public List<Mesas> listarMesas() {
        return mesasRepository.findAll();
    }
}
