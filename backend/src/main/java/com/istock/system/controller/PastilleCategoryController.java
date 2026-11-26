package com.istock.system.controller;

import com.istock.system.entities.PastilleCategory;
import com.istock.system.repository.PastilleCategoryRepository;
import com.istock.system.repository.PastilleRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/pastilles/categories")
public class PastilleCategoryController {
    private final PastilleCategoryRepository categories;

    private final PastilleRepository pastilles;

    public PastilleCategoryController(PastilleCategoryRepository categories, PastilleRepository pastilles) {
        this.pastilles = pastilles;
        this.categories = categories;
    }

    public record CategoryName(@NotBlank @Size(max = 60) String name) {}

    @GetMapping
    public List<PastilleCategoryRepository.CategoryRow> list() { return categories.listWithUsage(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public PastilleCategory create(@Valid @RequestBody CategoryName body) {
        return save(new PastilleCategory(), body.name());
    }

    @PutMapping("/{id}")
    @Transactional
    public PastilleCategory rename(@PathVariable Long id, @Valid @RequestBody CategoryName body) {
        return save(categories.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada.")), body.name());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public PastilleCategory delete(@PathVariable Long id) {
        var category = categories.findForDeletion(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));
        if (pastilles.existsByCategoryId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Remove the pastille links before deleting this category.");
        }
        categories.delete(category);
        return category;
    }

    private PastilleCategory save(PastilleCategory category, String name) {
        var candidate = new PastilleCategory();
        candidate.rename(name);
        String key = candidate.getNormalizedName();
        if (key.equals("sem categoria") || categories.findByNormalizedName(key)
                .filter(other -> !Objects.equals(other.getId(), category.getId())).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta categoria já existe.");
        }
        category.rename(name);
        return categories.saveAndFlush(category);
    }
}
