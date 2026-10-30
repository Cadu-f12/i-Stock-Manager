package com.istock.system.inventory;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventory;
    private final com.istock.system.service.UserService users;
    public InventoryController(InventoryService inventory, com.istock.system.service.UserService users) { this.inventory = inventory; this.users = users; }
    public record CategoryInput(String name) {}
    @GetMapping public InventoryService.InventoryView list() { return inventory.list(); }
    @PostMapping("/products") public InventoryService.ProductView create(@RequestBody InventoryService.ProductInput input, Authentication auth) {
        return inventory.save(null, input, users.fetchUser(auth.getName()).getId());
    }
    @PutMapping("/products/{id}") public InventoryService.ProductView update(@PathVariable long id, @RequestBody InventoryService.ProductInput input, Authentication auth) {
        return inventory.save(id, input, users.fetchUser(auth.getName()).getId());
    }
    @PostMapping("/products/{id}/movements") public InventoryService.ProductView move(@PathVariable long id, @RequestBody InventoryService.MovementInput input, Authentication auth) {
        return inventory.move(id, input, users.fetchUser(auth.getName()).getId());
    }
    @PostMapping("/categories") public CategoryInput createCategory(@RequestBody CategoryInput input) {
        return new CategoryInput(inventory.createCategory(input.name()));
    }
    @DeleteMapping("/categories") public InventoryService.InventoryView deleteCategory(@RequestParam String name, Authentication auth) {
        return inventory.deleteCategory(name, users.fetchUser(auth.getName()).getId());
    }
}
