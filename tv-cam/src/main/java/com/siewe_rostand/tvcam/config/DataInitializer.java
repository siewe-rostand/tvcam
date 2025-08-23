package com.siewe_rostand.tvcam.config;

import com.siewe_rostand.tvcam.Discount.model.Discount;
import com.siewe_rostand.tvcam.Discount.repository.DiscountRepository;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import com.siewe_rostand.tvcam.Roles.Roles;
import com.siewe_rostand.tvcam.Roles.RolesRepository;
import com.siewe_rostand.tvcam.Users.models.Users;
import com.siewe_rostand.tvcam.Users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Initialisation des données par défaut
 *
 * @author rostand
 * @project tv-cam
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RolesRepository rolesRepository;
    private final DiscountRepository discountRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeRoles();
        initializeDiscounts();
        initializeDefaultUsers();
    }

    private void initializeRoles() {
        log.info("Initialisation des rôles par défaut...");

        createRoleIfNotExists("ADMIN", "Administrateur système");
        createRoleIfNotExists("CHEF_CABLEUR", "Chef câbleur - supervision d'équipe");
        createRoleIfNotExists("TECHNICIEN", "Technicien - maintenance et réparations");
        createRoleIfNotExists("RECOUVREUR", "Recouvreur - gestion des paiements");
        createRoleIfNotExists("MANAGER", "Manager - gestion générale");

        log.info("Rôles initialisés avec succès");
    }

    private void createRoleIfNotExists(String roleName, String description) {
        if (rolesRepository.findByName(roleName) == null) {
            Roles role = Roles.builder()
                    .name(roleName)
                    .description(description)
                    .build();
            rolesRepository.save(role);
            log.info("Rôle créé: {}", roleName);
        }
    }

    private void initializeDiscounts() {
        log.info("Initialisation des rabais par défaut...");

        // Rabais pour paiement trimestriel (5%)
        createDiscountIfNotExists(PaymentFrequency.QUARTERLY, new BigDecimal("5.00"),
                "Rabais de 5% pour paiement trimestriel");

        // Rabais pour paiement semestriel (8%)
        createDiscountIfNotExists(PaymentFrequency.SEMI_ANNUALLY, new BigDecimal("8.00"),
                "Rabais de 8% pour paiement semestriel");

        // Rabais pour paiement annuel (12%)
        createDiscountIfNotExists(PaymentFrequency.ANNUALLY, new BigDecimal("12.00"),
                "Rabais de 12% pour paiement annuel");

        log.info("Rabais initialisés avec succès");
    }

    private void createDiscountIfNotExists(PaymentFrequency frequency, BigDecimal percentage, String description) {
        if (discountRepository.findByPaymentFrequencyAndIsActiveTrue(frequency).isEmpty()) {
            Discount discount = Discount.builder()
                    .paymentFrequency(frequency)
                    .discountPercentage(percentage)
                    .description(description)
                    .isActive(true)
                    .build();
            discountRepository.save(discount);
            log.info("Rabais créé: {} - {}%", frequency.name(), percentage);
        }
    }

    private void initializeDefaultUsers() {
        log.info("Initialisation des utilisateurs par défaut...");

        // Créer un admin par défaut s'il n'existe pas
        if (usersRepository.findByTelephone("123456789") == null) {
            Roles adminRole = rolesRepository.findByName("ADMIN");
            if (adminRole != null) {
                Users admin = Users.builder()
                        .firstname("Admin")
                        .lastname("System")
                        .telephone("123456789")
                        .password(passwordEncoder.encode("admin123"))
                        .roles(Set.of(adminRole))
                        .active(true)
                        .roles(Set.of(adminRole))
                        .build();

                usersRepository.save(admin);
                log.info("Utilisateur admin créé avec succès");
            }
        }

        log.info("Utilisateurs par défaut initialisés avec succès");
    }
}
