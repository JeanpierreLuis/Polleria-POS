package com.restaurantPos.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Productos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_producto;

    @ManyToOne
    @JoinColumn(name = "categoria")
    private Categorias categoria;

    private String nombre;

    private String descripcion;

    private BigDecimal precio;

    private Integer stock;

    private LocalDateTime fecha_registro;
}
