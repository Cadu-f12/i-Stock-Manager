package com.istock.system.inventory;

import com.istock.system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.text.Normalizer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class InventoryService {
    static final String UNCATEGORIZED = "Sem categoria";
    static final int MAX_STOCK = 999999999;
    private final InventoryProductRepository products;
    private final InventoryCategoryRepository categories;
    private final UserRepository users;

    public InventoryService(InventoryProductRepository products, InventoryCategoryRepository categories, UserRepository users) {
        this.products = products; this.categories = categories; this.users = users;
    }
    public record ProductInput(String name, String category, BigDecimal quantity, Long version) {}
    public record MovementInput(String direction, BigDecimal quantity, Long version) {}
    public record HistoryView(String author, Long userId, Instant at, List<String> changes) {}
    public record ProductView(Long id, Long version, String name, String category, int quantity, LocalDate updatedAt, List<HistoryView> history) {}
    public record InventoryView(List<ProductView> products, List<String> categories) {}

    @Transactional(readOnly = true)
    public InventoryView list() {
        var names = new ArrayList<String>();
        names.add(UNCATEGORIZED);
        categories.findAll().stream().map(InventoryCategory::getName).sorted().forEach(names::add);
        return new InventoryView(products.findAll().stream().map(this::view).toList(), names);
    }
    public String createCategory(String name) {
        name = validName(name, 60);
        String normalized = normalize(name);
        if (normalized.equals(normalize(UNCATEGORIZED)) || categories.findByNormalizedName(normalized).isPresent())
            throw error(HttpStatus.CONFLICT, "Esta categoria já existe.");
        var category = new InventoryCategory();
        category.name = name; category.normalizedName = normalized;
        return categories.saveAndFlush(category).getName();
    }
    public InventoryView deleteCategory(String name, long actor) {
        if (UNCATEGORIZED.equals(name)) throw error(HttpStatus.BAD_REQUEST, "A categoria padrão não pode ser excluída.");
        var category = category(name);
        for (var product : products.findAll()) {
            if (product.category != null && product.category.getId().equals(category.getId())) {
                product.category = null;
                record(product, actor, "Categoria: " + category.getName() + " → " + UNCATEGORIZED + " (categoria excluída)");
            }
        }
        products.flush();
        categories.delete(category);
        categories.flush();
        return list();
    }
    public ProductView save(Long id, ProductInput input, long actor) {
        String name = validName(input.name(), 120);
        int quantity = quantity(input.quantity(), false);
        var category = category(input.category());
        var product = id == null ? new InventoryProduct() : find(id);
        if (id != null) checkVersion(product, input.version());
        var changes = new ArrayList<String>();
        if (id == null) changes.add("Produto cadastrado com " + input.quantity() + " unidade(s).");
        else {
            if (!product.name.equals(name)) changes.add("Nome: " + product.name + " → " + name);
            String categoryName = category == null ? UNCATEGORIZED : category.getName();
            if (!categoryName(product).equals(categoryName)) changes.add("Categoria: " + categoryName(product) + " → " + categoryName);
            if (product.quantity != quantity) changes.add("Quantidade: " + product.quantity + " → " + quantity);
        }
        product.name = name; product.category = category; product.quantity = quantity;
        if (!changes.isEmpty()) record(product, actor, String.join("\n", changes));
        return view(products.saveAndFlush(product));
    }
    public ProductView move(long id, MovementInput input, long actor) {
        int quantity = quantity(input.quantity(), true);
        if (!("in".equals(input.direction()) || "out".equals(input.direction())))
            throw error(HttpStatus.BAD_REQUEST, "Informe uma movimentação válida.");
        var product = find(id);
        checkVersion(product, input.version());
        int before = product.quantity;
        long next = (long) before + ("in".equals(input.direction()) ? quantity : -quantity);
        if (next < 0 || next > MAX_STOCK) throw error(HttpStatus.CONFLICT, "Saldo insuficiente ou limite de estoque excedido.");
        product.quantity = (int) next;
        record(product, actor, ("in".equals(input.direction()) ? "Entrada" : "Saída") + " de " + input.quantity() + " unidade(s): " + before + " → " + next);
        return view(products.saveAndFlush(product));
    }
    private InventoryProduct find(long id) {
        return products.findById(id).orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Produto não encontrado."));
    }
    private int quantity(BigDecimal value, boolean movement) {
        try {
            int quantity = value == null ? -1 : value.intValueExact();
            if (quantity < (movement ? 1 : 0) || quantity > MAX_STOCK) throw new ArithmeticException();
            return quantity;
        } catch (ArithmeticException ex) {
            throw error(HttpStatus.BAD_REQUEST, "Informe uma quantidade inteira válida.");
        }
    }
    private InventoryCategory category(String name) {
        if (UNCATEGORIZED.equals(name)) return null;
        if (name == null) throw error(HttpStatus.BAD_REQUEST, "Informe a categoria.");
        return categories.findByNormalizedName(normalize(name)).orElseThrow(() -> error(HttpStatus.CONFLICT, "A categoria não existe mais. Atualize o estoque."));
    }
    private void checkVersion(InventoryProduct product, Long version) {
        if (!Objects.equals(product.version, version)) throw error(HttpStatus.CONFLICT, "Este produto foi alterado. Atualize o estoque antes de tentar novamente.");
    }
    private void record(InventoryProduct product, long actor, String description) {
        var user = users.findById(actor).orElseThrow(() -> error(HttpStatus.UNAUTHORIZED, "Sessão inválida."));
        product.updatedAt = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        product.history.add(0, new InventoryProduct.History(user.getName(), actor, description));
    }
    private String categoryName(InventoryProduct product) { return product.category == null ? UNCATEGORIZED : product.category.getName(); }
    private ProductView view(InventoryProduct p) {
        return new ProductView(p.id, p.version, p.name, categoryName(p), p.quantity, p.updatedAt,
            p.history.stream().map(h -> new HistoryView(h.author, h.userId, h.at, Arrays.asList(h.description.split("\n")))).toList());
    }
    private String validName(String value, int limit) {
        String name = value == null ? "" : value.trim().replaceAll("\\s+", " ");
        if (name.isEmpty() || name.length() > limit) throw error(HttpStatus.BAD_REQUEST, "Informe um nome de até " + limit + " caracteres.");
        return name;
    }
    private String normalize(String value) {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
    private ResponseStatusException error(HttpStatus status, String message) { return new ResponseStatusException(status, message); }
}
