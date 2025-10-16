package com.siewe_rostand.tvcam.Zone.service;

import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Customers.repository.CustomersRepository;
import com.siewe_rostand.tvcam.Users.models.Users;
import com.siewe_rostand.tvcam.Users.repository.UsersRepository;
import com.siewe_rostand.tvcam.Zone.dto.ZoneMapper;
import com.siewe_rostand.tvcam.Zone.dto.ZoneRequest;
import com.siewe_rostand.tvcam.Zone.dto.ZoneResponse;
import com.siewe_rostand.tvcam.Zone.model.Zone;
import com.siewe_rostand.tvcam.Zone.repository.ZoneRepository;
import com.siewe_rostand.tvcam.common.constraints.validator.ObjectsValidator;
import com.siewe_rostand.tvcam.security.services.ZoneSecurityService;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityNotFoundException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;

/**
 * Implémentation du service Zone
 *
 * @author rostand
 * @project tv-cam
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final UsersRepository usersRepository;
    private final CustomersRepository customersRepository;
    private final ZoneSecurityService zoneSecurityService;
    private final ObjectsValidator<ZoneRequest> validator;
    private final ZoneMapper zoneMapper;

    @Override
    @Transactional
    public ZoneResponse createZone(ZoneRequest request) {
        log.info("Création d'une nouvelle zone: {}", request.getName());
        validator.validate(request);

        // Seuls les admins peuvent créer des zones
        if (!zoneSecurityService.isAdmin()) {
            throw new RuntimeException("Seuls les administrateurs peuvent créer des zones");
        }

        Zone zone = zoneMapper.toZone(request);
        Zone savedZone = zoneRepository.save(zone);

        log.info("Zone créée avec succès. ID: {}", savedZone.getZoneId());
        return zoneMapper.toResponse(savedZone);
    }

    @Override
    @Transactional
    public ZoneResponse updateZone(Long zoneId, ZoneRequest request) {
        log.info("Mise à jour de la zone ID: {}", zoneId);
        validator.validate(request);

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone non trouvée avec l'ID: " + zoneId));

        // Vérifier l''accès
        if (!zoneSecurityService.isAdmin() && !zoneSecurityService.hasAccessToZone(zoneId)) {
            throw new RuntimeException("Accès refusé : vous n'avez pas accès à cette zone");
        }

        zoneMapper.updateZoneFromRequest(request, zone);
        Zone updatedZone = zoneRepository.save(zone);

        return zoneMapper.toResponse(updatedZone);
    }

    @Override
    @Transactional
    public HttpResponse<Object> deleteZone(Long zoneId) {
        log.info("Suppression de la zone ID: {}", zoneId);

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone non trouvée avec l'ID: " + zoneId));

        // Seuls les admins peuvent supprimer des zones
        if (!zoneSecurityService.isAdmin()) {
            throw new RuntimeException("Seuls les administrateurs peuvent supprimer des zones");
        }

        // Vérifier qu'il n'y a pas de clients assignés
        List<Customers> customers = customersRepository.findByZone(zone);
        if (!customers.isEmpty()) {
            throw new RuntimeException("Impossible de supprimer une zone qui contient des clients. " +
                    "Veuillez d'abord réassigner les " + customers.size() + " clients.");
        }

        // Supprimer les associations avec les utilisateurs
        List<Users> users = usersRepository.findByZones(zone);
        for (Users user : users) {
            user.getZones().remove(zone);
            usersRepository.save(user);
        }

        zoneRepository.delete(zone);

        return HttpResponse.builder()
                .message("Zone supprimée avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .timestamp(now()).success(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ZoneResponse getZoneById(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone non trouvée avec l'ID: " + zoneId));

        // Vérifier l'accès
        if (!zoneSecurityService.isAdmin() && !zoneSecurityService.hasAccessToZone(zoneId)) {
            throw new RuntimeException("Accès refusé : vous n'avez pas accès à cette zone");
        }

        return zoneMapper.toResponse(zone);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ZoneResponse> getAllZones(Integer page, Integer size, String sortBy, String direction,
                                                       String searchTerm) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);

        Page<Zone> zonesPage;

        if (zoneSecurityService.isAdmin()) {
            // Admin voit toutes les zones
            if (searchTerm != null && !searchTerm.isEmpty()) {
                zonesPage = zoneRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        searchTerm, searchTerm, pageable);
            } else {
                zonesPage = zoneRepository.findAll(pageable);
            }
        } else {
            // Utilisateur normal voit ses zones uniquement
            List<Long> userZoneIds = zoneSecurityService.getCurrentUserZoneIds();
            zonesPage = zoneRepository.findByZoneIdIn(userZoneIds, pageable);
        }

        List<ZoneResponse> zoneResponses = zonesPage.getContent().stream()
                .map(zoneMapper::toResponse)
                .collect(Collectors.toList());

        return PaginatedResponse.<ZoneResponse>builder()
                .data(zoneResponses)
                .page(page)
                .totalElements((int) zonesPage.getTotalElements())
                .totalPages(zonesPage.getTotalPages())
                .firstPage(zonesPage.isFirst())
                .lastPage(zonesPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZoneResponse> getActiveZones() {
        List<Zone> activeZones;

        if (zoneSecurityService.isAdmin()) {
            activeZones = zoneRepository.findByIsActiveTrue();
        } else {
            List<Long> userZoneIds = zoneSecurityService.getCurrentUserZoneIds();
            activeZones = zoneRepository.findByZoneIdInAndIsActiveTrue(userZoneIds);
        }

        return activeZones.stream()
                .map(zoneMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZoneResponse> getZonesByUserId(Long userId) {
        log.debug("Récupération des zones pour l'utilisateur: {}", userId);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l''ID: " + userId));

        Set<Zone> userZones = user.getZones();
        return userZones.stream()
                .map(zoneMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HttpResponse<Object> assignUserToZone(Long zoneId, Long userId) {
        log.info("Assignation de l''utilisateur {} à la zone {}", userId, zoneId);

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone non trouvée avec l''ID: " + zoneId));

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l''ID: " + userId));

        // Seuls les admins peuvent faire des assignations
        if (!zoneSecurityService.isAdmin()) {
            throw new RuntimeException("Seuls les administrateurs peuvent assigner des utilisateurs aux zones");
        }

        // Vérifier si l''utilisateur n''est pas déjà assigné
        if (!user.getZones().contains(zone)) {
            user.getZones().add(zone);
            usersRepository.save(user);
            log.info("Utilisateur {} assigné avec succès à la zone {}", userId, zoneId);
        } else {
            log.info("Utilisateur {} déjà assigné à la zone {}", userId, zoneId);
        }

        return HttpResponse.builder()
                .message("Utilisateur assigné à la zone avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .timestamp(now()).success(true)
                .build();
    }

    @Override
    @Transactional
    public HttpResponse<Object> removeUserFromZone(Long zoneId, Long userId) {
        log.info("Suppression de l''utilisateur {} de la zone {}", userId, zoneId);

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone non trouvée avec l''ID: " + zoneId));

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l''ID: " + userId));

        // Seuls les admins peuvent faire des dés-assignations
        if (!zoneSecurityService.isAdmin()) {
            throw new RuntimeException("Seuls les administrateurs peuvent retirer des utilisateurs des zones");
        }

        if (user.getZones().contains(zone)) {
            user.getZones().remove(zone);
            usersRepository.save(user);
            log.info("Utilisateur {} retiré avec succès de la zone {}", userId, zoneId);
        } else {
            log.info("Utilisateur {} n''était pas assigné à la zone {}", userId, zoneId);
        }

        return HttpResponse.builder()
                .message("Utilisateur retiré de la zone avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .timestamp(now()).success(true)
                .build();
    }

    @Override
    @Transactional
    public HttpResponse<Object> assignCustomerToZone(Long zoneId, Long customerId) {
        log.info("Assignation du client {} à la zone {}", customerId, zoneId);

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone non trouvée avec l'ID: " + zoneId));

        Customers customer = customersRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec l'ID: " + customerId));

        // Vérifier l''accès à la zone
        if (!zoneSecurityService.isAdmin() && !zoneSecurityService.hasAccessToZone(zoneId)) {
            throw new RuntimeException("Accès refusé : vous n'avez pas accès à cette zone");
        }

        customer.setZone(zone);
        customersRepository.save(customer);

        log.info("Client {} assigné avec succès à la zone {}", customerId, zoneId);

        return HttpResponse.builder()
                .message("Client assigné à la zone avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .timestamp(now()).success(true)
                .build();
    }
}
