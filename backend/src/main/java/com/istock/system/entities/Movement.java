package com.istock.system.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "movement")
public class Movement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "pastille_id",
            nullable = false
    )
    private Pastille pastille;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reason_movement_id",
            nullable = false
    )
    private ReasonMovement reasonMovement;

    @Column(name = "quantity", nullable = false, columnDefinition = "INT CHECK (quantity > 0)")
    private Integer quantity;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime = LocalDateTime.now();
}
