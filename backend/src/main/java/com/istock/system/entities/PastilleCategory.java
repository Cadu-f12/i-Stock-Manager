package com.istock.system.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.text.Normalizer;
import java.util.Locale;

@Entity
@Table(name = "pastille_category")
public class PastilleCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 60)
    private String name;
    @Column(nullable = false, unique = true)
    private String normalizedName;

    public Long getId() { return id; }
    public String getName() { return name; }
    @JsonIgnore
    public String getNormalizedName() { return normalizedName; }

    public void rename(String name) {
        this.name = name.strip().replaceAll("\\s+", " ");
        normalizedName = Normalizer.normalize(this.name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
