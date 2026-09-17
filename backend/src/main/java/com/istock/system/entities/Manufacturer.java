package com.istock.system.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "manufacturer")
public class Manufacturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 120)
    private String name;

    @Column(name = "contact", length = 120, nullable = false)
    private String contact;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "manufacturer", fetch = FetchType.LAZY)
    private List<Pastille> pastilles;

    public Manufacturer() {}

    public Manufacturer(Long id, List<Pastille> pastilles, String name, String contact, Boolean isActive) {
        this.id = id;
        this.pastilles = pastilles;
        this.name = name;
        this.contact = contact;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Pastille> getPastilles() {
        return pastilles;
    }

    public void setPastilles(List<Pastille> pastilles) {
        this.pastilles = pastilles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
