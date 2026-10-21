package com.istock.system.inventory;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventory;
    public InventoryController(InventoryService inventory) { this.inventory = inventory; }
    public record CategoryInput(String name) {}
    @GetMapping public InventoryService.InventoryView list() { return inventory.list(); }
    @PostMapping("/products") public InventoryService.ProductView create(@RequestBody InventoryService.ProductInput input, Authentication auth) {
        return inventory.save(null, input, Long.parseLong(auth.getName()));
    }
    @PutMapping("/products/{id}") public InventoryService.ProductView update(@PathVariable long id, @RequestBody InventoryService.ProductInput input, Authentication auth) {
        return inventory.save(id, input, Long.parseLong(auth.getName()));
    }
    @PostMapping("/products/{id}/movements") public InventoryService.ProductView move(@PathVariable long id, @RequestBody InventoryService.MovementInput input, Authentication auth) {
        return inventory.move(id, input, Long.parseLong(auth.getName()));
    }
    @PostMapping("/categories") public CategoryInput createCategory(@RequestBody CategoryInput input) {
        return new CategoryInput(inventory.createCategory(input.name()));
    }
    @DeleteMapping("/categories") public InventoryService.InventoryView deleteCategory(@RequestParam String name, Authentication auth) {
        return inventory.deleteCategory(name, Long.parseLong(auth.getName()));
    }
}
