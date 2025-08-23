package com.siewe_rostand.tvcam.security.services;

import com.siewe_rostand.tvcam.Users.models.Users;
import com.siewe_rostand.tvcam.Users.repository.UsersRepository;
import com.siewe_rostand.tvcam.Zone.model.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour gérer la sécurité basée sur les zones
 * 
 * @author rostand
 * @project tv-cam
 */
@Service
@RequiredArgsConstructor
public class ZoneSecurityService {

    private final UsersRepository usersRepository;

    /**
     * Récupère l'utilisateur connecté actuel
     */
    public Users getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = usersRepository.findByTelephone(username);
        if (user == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        return user;
    }

    /**
     * Récupère les IDs des zones accessibles par l'utilisateur connecté
     */
    public List<Long> getCurrentUserZoneIds() {
        Users currentUser = getCurrentUser();
        return currentUser.getZones().stream()
                .map(Zone::getZoneId)
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si l'utilisateur connecté a accès à une zone spécifique
     */
    public boolean hasAccessToZone(Long zoneId) {
        List<Long> userZoneIds = getCurrentUserZoneIds();
        return userZoneIds.contains(zoneId);
    }

    /**
     * Vérifie si l'utilisateur connecté a accès à un client (via la zone du client)
     */
    public boolean hasAccessToCustomer(Long customerId) {
        // TODO: Implémenter la vérification d'accès au client
        // Ceci nécessiterait une requête pour récupérer la zone du client
        return true; // Pour l'instant, permettre l'accès
    }

    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     */
    public boolean hasRole(String roleName) {
        Users currentUser = getCurrentUser();
        return currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    /**
     * Vérifie si l'utilisateur est un chef câbleur
     */
    public boolean isChefCableur() {
        return hasRole("CHEF_CABLEUR");
    }

    /**
     * Vérifie si l'utilisateur est un technicien
     */
    public boolean isTechnicien() {
        return hasRole("TECHNICIEN");
    }

    /**
     * Vérifie si l'utilisateur est un recouvreur
     */
    public boolean isRecouvreur() {
        return hasRole("RECOUVREUR");
    }

    /**
     * Vérifie si l'utilisateur est un administrateur
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
}
