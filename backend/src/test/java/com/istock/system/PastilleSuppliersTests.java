package com.istock.system;

import com.istock.system.dtos.request.RegistryPastilleRequest;
import com.istock.system.dtos.request.UpdatePastilleRequest;
import com.istock.system.entities.Manufacturer;
import com.istock.system.entities.Supplier;
import com.istock.system.repository.ManufacturerRepository;
import com.istock.system.repository.SupplierRepository;
import com.istock.system.service.PastilleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PastilleSuppliersTests {
    @Autowired ManufacturerRepository manufacturers;
    @Autowired SupplierRepository suppliers;
    @Autowired PastilleService pastilles;
    @Autowired com.istock.system.controller.PastilleCategoryController categories;

    @Test
    void categoryCanBeAssignedRenamedAndCleared() {
        var maker = new Manufacturer();
        maker.setName("Fabricante de categorias");
        maker.setContact("Contato");
        var makerId = manufacturers.save(maker).getId();
        var category = categories.create(new com.istock.system.controller.PastilleCategoryController.CategoryName("Fresamento"));
        assertThrows(ResponseStatusException.class, () -> categories.create(
                new com.istock.system.controller.PastilleCategoryController.CategoryName(" Frésamento ")));
        var product = pastilles.registerPastille(new RegistryPastilleRequest(
                "TEST-CATEGORY", "Pastilha", 1, makerId, null, new com.istock.system.dtos.request.CategorySelection(category.getId())));
        categories.rename(category.getId(), new com.istock.system.controller.PastilleCategoryController.CategoryName("Fresa"));
        pastilles.updateById(product.id(), new UpdatePastilleRequest("Editada", makerId, 1, null, null));
        assertEquals("Fresa", pastilles.findAll(false).stream()
                .filter(p -> p.id().equals(product.id())).findFirst().orElseThrow().category().name());
        pastilles.updateById(product.id(), new UpdatePastilleRequest("Editada", makerId, 1, null,
                new com.istock.system.dtos.request.CategorySelection(null)));
        assertNull(pastilles.findById(product.id()).category());
    }

    @Test
    void listsLinkedSuppliersAndPreservesThemUntilExplicitlyChanged() {
        var maker = new Manufacturer();
        maker.setName("Fabricante teste");
        maker.setContact("Contato teste");
        var makerId = manufacturers.save(maker).getId();
        var a = suppliers.save(new Supplier(null, "A", "Contato A", true)).getId();
        var b = suppliers.save(new Supplier(null, "B", "Contato B", true)).getId();
        var product = pastilles.registerPastille(new RegistryPastilleRequest(
                "TEST-SUPPLIERS", "Pastilha", 1, makerId, Set.of(a, b), null));
        pastilles.updateById(product.id(), new UpdatePastilleRequest("Editada", makerId, 1, null, null));
        assertThrows(ResponseStatusException.class, () -> pastilles.updateById(product.id(),
                new UpdatePastilleRequest("Inválida", makerId, 1, Set.of(Long.MAX_VALUE), null)));
        var listed = pastilles.findAll(true).stream().filter(p -> p.id().equals(product.id())).findFirst().orElseThrow();
        assertEquals(List.of("A", "B"), listed.suppliers());
        assertEquals("Editada", listed.description());
        pastilles.updateById(product.id(), new UpdatePastilleRequest("Editada", makerId, 1, Set.of(), null));
        assertTrue(pastilles.findById(product.id()).suppliers().isEmpty());
    }
}
