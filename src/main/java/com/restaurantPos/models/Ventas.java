package com.restaurantPos.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ventas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido")
    private Pedidos pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja")
    private Caja caja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cajero")
    private Usuarios usuario;

    private Double total;

    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago")
    private EstadoPago estadopago;

    public enum EstadoPago {
        PENDIENTE,
        PAGADO,
        CANCELADO
    }
}
