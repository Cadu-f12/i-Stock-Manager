package com.istock.system.seed;

import com.istock.system.entities.*;
import com.istock.system.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final SupplierRepository supplierRepository;
    private final PastilleRepository pastilleRepository;
    private final ReasonMovementRepository reasonMovementRepository;
    private final MovementRepository movementRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
            UserRepository userRepository,
            ManufacturerRepository manufacturerRepository,
            SupplierRepository supplierRepository,
            PastilleRepository pastilleRepository,
            ReasonMovementRepository reasonMovementRepository,
            MovementRepository movementRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.supplierRepository = supplierRepository;
        this.pastilleRepository = pastilleRepository;
        this.reasonMovementRepository = reasonMovementRepository;
        this.movementRepository = movementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (!hasSeedArgument(args)) {
            return;
        }

        System.out.println("=================================");
        System.out.println("🌱 Executando database seed...");
        System.out.println("=================================");

        seedUser();
        seedManufacturers();
        seedSuppliers();
        seedMovementReasons();
        seedPastilles();
        seedMovements();

        System.out.println("=================================");
        System.out.println("✅ Database seed finalizado.");
        System.out.println("=================================");
    }

    private boolean hasSeedArgument(String[] args) {
        for (String arg : args) {
            if ("--seed".equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private void seedUser() {

        if (userRepository.findByEmail("admin@istock.com").isPresent()) {
            System.out.println("→ Usuário admin já existe.");
            return;
        }

        User admin = new User(
                null,                              // id
                "ADMIN",                           // role
                "Admin",                           // name
                "admin@istock.com",                // email
                passwordEncoder.encode("admin"),   // password
                true,                              // isActive
                List.of()                          // movements
        );

        userRepository.save(admin);

        System.out.println("→ Usuário admin criado.");
    }

    private void seedManufacturers() {

        createManufacturerIfMissing("Sandvik", "contato@sandvik.com");
        createManufacturerIfMissing("Kennametal", "contato@kennametal.com");
        createManufacturerIfMissing("Mitsubishi Materials", "contato@mitsubishimaterials.com");
        createManufacturerIfMissing("Iscar", "contato@iscar.com");
        createManufacturerIfMissing("Walter", "contato@walter-tools.com");
        createManufacturerIfMissing("Dormer Pramet", "contato@dormerpramet.com");
        System.out.println("→ Fabricantes conferidos.");
    }

    private void createManufacturerIfMissing(String name, String email) {
        if (manufacturerRepository.findAll().stream().noneMatch(item -> item.getName().equalsIgnoreCase(name))) {
            manufacturerRepository.save(new Manufacturer(null, List.of(), name, email, true));
        }
    }

    private void seedSuppliers() {

        createSupplierIfMissing("Fornecedor Alpha", "alpha@email.com");
        createSupplierIfMissing("Fornecedor Beta", "beta@email.com");
        createSupplierIfMissing("Ferramentas Brasil", "vendas@ferramentasbrasil.com");
        createSupplierIfMissing("Usinagem Central", "compras@usinagemcentral.com");
        System.out.println("→ Fornecedores conferidos.");
    }

    private void createSupplierIfMissing(String name, String email) {
        if (supplierRepository.findAll().stream().noneMatch(item -> item.getName().equalsIgnoreCase(name))) {
            supplierRepository.save(new Supplier(null, name, email, true));
        }
    }

    private void seedMovementReasons() {

        createReasonIfMissing("Compra", MovementType.IN);
        createReasonIfMissing("Devolução", MovementType.IN);
        createReasonIfMissing("Ajuste de inventário", MovementType.IN);
        createReasonIfMissing("Consumo em usinagem", MovementType.OUT);
        createReasonIfMissing("Descarte", MovementType.OUT);
        createReasonIfMissing("Amostra / teste", MovementType.OUT);
        System.out.println("→ Motivos de movimentação conferidos.");
    }

    private void createReasonIfMissing(String description, MovementType type) {
        if (reasonMovementRepository.findAll().stream().noneMatch(item -> item.getDescription().equalsIgnoreCase(description))) {
            reasonMovementRepository.save(new ReasonMovement(null, description, type, true, List.of()));
        }
    }

    private void seedPastilles() {

        List<Manufacturer> manufacturers = manufacturerRepository.findAll();
        List<Supplier> suppliers = supplierRepository.findAll();

        if (manufacturers.isEmpty() || suppliers.isEmpty()) {
            System.out.println("→ Não foi possível criar as pastilhas.");
            return;
        }

        Map<String, Manufacturer> manufacturerByName = manufacturers.stream()
                .collect(Collectors.toMap(Manufacturer::getName, Function.identity()));
        List<Pastille> pastilles = List.of(
                demoPastille("SKU-001", "Inserto torneamento CNMG 120408-PM", 12, manufacturerByName.get("Sandvik"), suppliers, 0, 1),
                demoPastille("SKU-002", "Inserto fresamento APKT 1604", 8, manufacturerByName.get("Kennametal"), suppliers, 0, 2),
                demoPastille("SKU-003", "Inserto acabamento VBMT 160404", 10, manufacturerByName.get("Iscar"), suppliers, 0, 0),
                demoPastille("SKU-004", "Inserto torneamento rosca MGMN 300", 6, manufacturerByName.get("Mitsubishi Materials"), suppliers, 0, 3),
                demoPastille("SKU-005", "Inserto fresamento WNMG 080408", 15, manufacturerByName.get("Walter"), suppliers, 0, 1),
                demoPastille("SKU-006", "Inserto canal 3 mm", 5, manufacturerByName.get("Dormer Pramet"), suppliers, 0, 2),
                demoPastille("SKU-007", "Inserto torneamento alumínio CCGT 09T304", 10, manufacturerByName.get("Sandvik"), suppliers, 0, 0),
                demoPastille("SKU-008", "Inserto fresamento alto avanço LNMU 0303", 7, manufacturerByName.get("Kennametal"), suppliers, 0, 1),
                demoPastille("SKU-009", "Inserto torneamento pesado SNMG 1204", 20, manufacturerByName.get("Iscar"), suppliers, 0, 2),
                demoPastille("SKU-010", "Inserto acabamento DCMT 11T304", 9, manufacturerByName.get("Walter"), suppliers, 0, 3),
                demoPastille("SKU-011", "Inserto fresamento SEKT 1204", 12, manufacturerByName.get("Mitsubishi Materials"), suppliers, 0, 0),
                demoPastille("SKU-012", "Inserto torneamento inox CCMT 09T308", 8, manufacturerByName.get("Dormer Pramet"), suppliers, 0, 1)
        );
        List<Pastille> toCreate = pastilles.stream()
                .filter(item -> !pastilleRepository.existsBySkuCode(item.getSkuCode()))
                .toList();
        pastilleRepository.saveAll(toCreate);
        System.out.println("→ " + toCreate.size() + " pastilhas criadas.");
    }

    private Pastille demoPastille(String sku, String description, int minimum, Manufacturer manufacturer,
                                  List<Supplier> suppliers, int balance, int supplierOffset) {
        return new Pastille(null, sku, description, minimum, balance, manufacturer,
                List.of(suppliers.get(supplierOffset % suppliers.size()), suppliers.get((supplierOffset + 1) % suppliers.size())), List.of());
    }

    private void seedMovements() {
        if (movementRepository.count() > 0) {
            System.out.println("→ Movimentações já cadastradas; histórico de demonstração não duplicado.");
            return;
        }
        User user = userRepository.findByEmail("admin@istock.com").orElse(null);
        List<Pastille> pastilles = pastilleRepository.findAll();
        List<ReasonMovement> reasons = reasonMovementRepository.findAll();
        if (user == null || pastilles.isEmpty() || reasons.isEmpty()) {
            System.out.println("→ Não foi possível criar as movimentações.");
            return;
        }
        Map<String, ReasonMovement> reasonByDescription = reasons.stream()
                .collect(Collectors.toMap(ReasonMovement::getDescription, Function.identity()));
        LocalDateTime now = LocalDateTime.now();
        List<Movement> movements = new java.util.ArrayList<>();
        for (int i = 0; i < pastilles.size(); i++) {
            Pastille pastille = pastilles.get(i);
            int initialStock = 28 + (i * 7 % 35);
            int firstUse = 3 + (i % 6);
            int returnQty = 1 + (i % 3);
            int secondUse = 2 + (i * 2 % 7);
            int disposalQty = 1 + (i % 2);
            movements.add(new Movement(null, pastille, user, requireReason(reasonByDescription, "Compra"), initialStock, now.minusDays(45L - i)));
            movements.add(new Movement(null, pastille, user, requireReason(reasonByDescription, "Consumo em usinagem"), firstUse, now.minusDays(32L - i)));
            movements.add(new Movement(null, pastille, user, requireReason(reasonByDescription, "Devolução"), returnQty, now.minusDays(20L - i)));
            movements.add(new Movement(null, pastille, user, requireReason(reasonByDescription, "Consumo em usinagem"), secondUse, now.minusDays(9L - i % 7)));
            movements.add(new Movement(null, pastille, user, requireReason(reasonByDescription, "Descarte"), disposalQty, now.minusDays(i % 5)));
            pastille.setCurrentBalance(initialStock - firstUse + returnQty - secondUse - disposalQty);
        }
        movementRepository.saveAll(movements);
        pastilleRepository.saveAll(pastilles);
        System.out.println("→ " + movements.size() + " movimentações de demonstração criadas.");
    }

    private ReasonMovement requireReason(Map<String, ReasonMovement> reasons, String description) {
        ReasonMovement reason = reasons.get(description);
        if (reason == null) {
            throw new IllegalStateException("Missing seed movement reason: " + description);
        }
        return reason;
    }
}
