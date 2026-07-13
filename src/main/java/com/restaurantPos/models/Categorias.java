package com.restaurantPos.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Categoria")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Categorias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_Categoria;

    @Column(name = "nombre")
    private String nombre;

    private String descripcion;
}
