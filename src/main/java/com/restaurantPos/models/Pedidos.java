package com.restaurantPos.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedidos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuarios usuario;

    @ManyToOne
    @JoinColumn(name = "id_mesa")
    private Mesas mesa;

    @Column(name = "tipos_pedido")
    private String tipoPedido;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "fecha")
    private LocalDateTime fecha;
}
