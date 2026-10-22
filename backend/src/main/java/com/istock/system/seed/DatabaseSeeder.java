package com.istock.system.seed;

import com.istock.system.entities.*;
import com.istock.system.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final SupplierRepository supplierRepository;
    private final PastilleRepository pastilleRepository;
    private final ReasonMovementRepository reasonMovementRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
            UserRepository userRepository,
            ManufacturerRepository manufacturerRepository,
            SupplierRepository supplierRepository,
            PastilleRepository pastilleRepository,
            ReasonMovementRepository reasonMovementRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.supplierRepository = supplierRepository;
        this.pastilleRepository = pastilleRepository;
        this.reasonMovementRepository = reasonMovementRepository;
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

        if (manufacturerRepository.count() > 0) {
            System.out.println("→ Fabricantes já cadastrados.");
            return;
        }

        Manufacturer sandvik = new Manufacturer(
                null, List.of(), "Sandvik", "contato@sandvik.com", true
        );

        Manufacturer kennametal = new Manufacturer(
                null, List.of(), "Kennametal", "contato@kennametal.com", true
        );

        manufacturerRepository.saveAll(List.of(sandvik, kennametal));

        System.out.println("→ Fabricantes criados.");
    }

    private void seedSuppliers() {

        if (supplierRepository.count() > 0) {
            System.out.println("→ Fornecedores já cadastrados.");
            return;
        }

        Supplier supplierOne = new Supplier(
                null, "Fornecedor Alpha", "alpha@email.com", true
        );

        Supplier supplierTwo = new Supplier(
                null, "Fornecedor Beta", "beta@email.com", true
        );

        supplierRepository.saveAll(List.of(supplierOne, supplierTwo));

        System.out.println("→ Fornecedores criados.");
    }

    private void seedMovementReasons() {

        if (reasonMovementRepository.count() > 0) {
            System.out.println("→ Motivos de movimentação já cadastrados.");
            return;
        }

        ReasonMovement purchase = new ReasonMovement(
                null, "Compra", MovementType.IN, true, List.of()
        );

        ReasonMovement returnReason = new ReasonMovement(
                null, "Devolução", MovementType.IN, true, List.of()
        );

        ReasonMovement consumption = new ReasonMovement(
                null, "Consumo em usinagem", MovementType.OUT, true, List.of()
        );

        ReasonMovement disposal = new ReasonMovement(
                null, "Descarte", MovementType.OUT, true, List.of()
        );

        reasonMovementRepository.saveAll(
                List.of(purchase, returnReason, consumption, disposal)
        );

        System.out.println("→ Motivos de movimentação criados.");
    }

    private void seedPastilles() {

        if (pastilleRepository.count() > 0) {
            System.out.println("→ Pastilhas já cadastradas.");
            return;
        }

        List<Manufacturer> manufacturers = manufacturerRepository.findAll();
        List<Supplier> suppliers = supplierRepository.findAll();

        if (manufacturers.isEmpty() || suppliers.isEmpty()) {
            System.out.println("→ Não foi possível criar as pastilhas.");
            return;
        }

        Manufacturer manufacturerOne = manufacturers.get(0);
        Manufacturer manufacturerTwo = manufacturers.size() > 1
                ? manufacturers.get(1)
                : manufacturerOne;

        Supplier supplierOne = suppliers.get(0);
        Supplier supplierTwo = suppliers.size() > 1
                ? suppliers.get(1)
                : supplierOne;

        Pastille pastilleOne = new Pastille(
                null,
                "SKU-001",
                "Pastilha de metal duro para torneamento",
                10,
                50,
                manufacturerOne,
                List.of(supplierOne),
                List.of()
        );

        Pastille pastilleTwo = new Pastille(
                null,
                "SKU-002",
                "Pastilha de metal duro para fresamento",
                5,
                20,
                manufacturerTwo,
                List.of(supplierOne, supplierTwo),
                List.of()
        );

        pastilleRepository.saveAll(List.of(pastilleOne, pastilleTwo));

        System.out.println("→ Pastilhas criadas.");
    }
}