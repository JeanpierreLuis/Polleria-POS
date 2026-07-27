package com.restaurantPos.services;

import com.restaurantPos.models.Clientes;
import com.restaurantPos.repositories.ClientesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientesService {
    private final ClientesRepository clientesRepository;

    public Clientes obtenerOCrearCliente(String nombre) {
        return clientesRepository.findByNombre(nombre).orElseGet(() -> {
            Clientes clientes = Clientes.builder()
                    .nombre(nombre)
                    .build();

            return clientesRepository.save(clientes);
        });
    }

}
