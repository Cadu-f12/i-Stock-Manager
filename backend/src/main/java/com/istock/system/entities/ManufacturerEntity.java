package com.istock.system.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class ManufacturerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long manufacturer_id;

    @OneToMany(mappedBy = "manufacturer", fetch = FetchType.LAZY)
    private List<PastilhaEntity> pastilhas;

    @Column(name = "name")
    private String name;

    @Column(name = "contact")
    private String contact;

    @Column(name = "is_active")
    private Boolean isActive;

    public ManufacturerEntity() {}

    public ManufacturerEntity(
            Long manufacturer_id,
            List<PastilhaEntity> pastilhas,
            String name,
            String contact,
            Boolean isActive
    ) {
        this.manufacturer_id = manufacturer_id;
        this.pastilhas = pastilhas;
        this.name = name;
        this.contact = contact;
        this.isActive = isActive;
    }

    public Long getManufacturer_id() {
        return manufacturer_id;
    }

    public void setManufacturer_id(Long manufacturer_id) {
        this.manufacturer_id = manufacturer_id;
    }

    public List<PastilhaEntity> getPastilhas() {
        return pastilhas;
    }

    public void setPastilhas(List<PastilhaEntity> pastilhas) {
        this.pastilhas = pastilhas;
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
