package com.restaurantPos.utils;

import com.restaurantPos.models.Pedidos;

import java.time.Duration;
import java.time.LocalDateTime;

public class TiempoAcumulado {

    public static String calcularTiempoEspera(Pedidos pedido){
        long segundos = Duration.between(
                pedido.getFecha(),
                LocalDateTime.now()
        ).getSeconds();

        long minutos = segundos / 60;
        long restoSegundos = segundos % 60;

        return minutos + "m " + restoSegundos + "s";
    }
}
