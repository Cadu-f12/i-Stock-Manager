package com.istock.system.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 120)
    private String contact;

    @Column(nullable = false)
    private Boolean isActive = true;

    @ManyToMany(mappedBy = "suppliers")
    private List<Pastille> pastilles;

    public Supplier() {}

    public Supplier(Long id, String name, String contact, Boolean isActive) {
        this.id = id;
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

    public List<Pastille> getPastilles() {
        return pastilles;
    }

    public void setPastilles(List<Pastille> pastilles) {
        this.pastilles = pastilles;
    }
}
