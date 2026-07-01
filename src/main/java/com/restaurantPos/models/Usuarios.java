package com.restaurantPos.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table (name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Usuarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    private String nombre;
    private String username;

    @Column(name = "password_hash")
    private String password;

    private String telefono;

    @Column(name = "id_rol")
    private Integer rol;
}
