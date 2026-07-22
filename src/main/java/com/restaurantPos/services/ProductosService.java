package com.restaurantPos.services;

import com.restaurantPos.models.Productos;
import com.restaurantPos.repositories.ProductosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductosService {

    private final ProductosRepository productosRepository;

    public Productos guardarProductos(Productos productos) {
        return productosRepository.save(productos);
    }

    public List<Productos> listarProductos() {
        return productosRepository.findAll();
    }

    public Productos buscarProductoPorId(Long id){
        return productosRepository.findById(id).orElse(null);
    }

    public void eiminarProducto(Long id){
        productosRepository.deleteById(id);
    }

}

