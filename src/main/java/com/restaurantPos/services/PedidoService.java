package com.restaurantPos.services;

import com.restaurantPos.models.Pedidos;
import com.restaurantPos.repositories.PedidosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidosRepository pedidosRepository;

    public Pedidos guardarPedido(Pedidos pedido){
        return pedidosRepository.save(pedido);
    }

    public List<Pedidos> listarTodos() {
        return pedidosRepository.findAll();
    }

    public Pedidos buscarPorId(Long id){
        return pedidosRepository.findById(id).orElse(null);
    }

    public void eliminar (Long id){
        pedidosRepository.deleteById(id);
    }
}
