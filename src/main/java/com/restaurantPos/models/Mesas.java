package com.restaurantPos.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Mesas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mesas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer mesas;

}
