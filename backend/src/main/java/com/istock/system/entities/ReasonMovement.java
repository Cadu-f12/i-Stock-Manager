package com.istock.system.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "reason_movement")
public class ReasonMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80, nullable = false)
    private String description;

    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "reasonMovement", fetch = FetchType.LAZY)
    private List<Movement> movements;

    public ReasonMovement() {}

    public ReasonMovement(
            Long id,
            String description,
            MovementType type,
            Boolean isActive,
            List<Movement> movements
    ) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.isActive = isActive;
        this.movements = movements;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<Movement> getMovements() {
        return movements;
    }

    public void setMovements(List<Movement> movements) {
        this.movements = movements;
    }
}
