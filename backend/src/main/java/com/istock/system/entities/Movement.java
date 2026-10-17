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

    public Movement() {}

    public Movement(Long id, Pastille pastille, User user, ReasonMovement reasonMovement, Integer quantity, LocalDateTime dateTime) {
        this.id = id;
        this.pastille = pastille;
        this.user = user;
        this.reasonMovement = reasonMovement;
        this.quantity = quantity;
        this.dateTime = dateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pastille getPastille() {
        return pastille;
    }

    public void setPastille(Pastille pastille) {
        this.pastille = pastille;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ReasonMovement getReasonMovement() {
        return reasonMovement;
    }

    public void setReasonMovement(ReasonMovement reasonMovement) {
        this.reasonMovement = reasonMovement;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
