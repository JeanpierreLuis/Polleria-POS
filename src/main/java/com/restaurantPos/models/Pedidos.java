package com.restaurantPos.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private long id_pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuarios usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_mesa")
    private Mesas mesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Clientes cliente;

    @OneToMany(mappedBy = "pedido")
    private List<DetallePedido> detalles;

    @Column(name = "tipo_pedido")
    private String tipoPedido;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    public enum EstadoPedido {
        PENDIENTE,
        PREPARANDO,
        LISTO,
        ANULADO
    }
}
