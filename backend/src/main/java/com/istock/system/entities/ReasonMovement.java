package com.istock.system.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "reason_movement")
public class ReasonMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80, nullable = false)
    private String descricao;

    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private Boolean isActive = true;

    public ReasonMovement() {}

    public ReasonMovement(Long id, String descricao, MovementType type, Boolean isActive) {
        this.id = id;
        this.descricao = descricao;
        this.type = type;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public MovementType getType() {
        return type;
    }

    public void setType(MovementType type) {
        this.type = type;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
