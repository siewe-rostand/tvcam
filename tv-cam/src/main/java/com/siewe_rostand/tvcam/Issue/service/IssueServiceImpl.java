package com.siewe_rostand.tvcam.Issue.service;

import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Customers.services.CustomerService;
import com.siewe_rostand.tvcam.Issue.dto.IssueMapper;
import com.siewe_rostand.tvcam.Issue.dto.IssueRequest;
import com.siewe_rostand.tvcam.Issue.dto.IssueResponse;
import com.siewe_rostand.tvcam.Issue.model.Issue;
import com.siewe_rostand.tvcam.Issue.model.enumeration.IssueStatus;
import com.siewe_rostand.tvcam.Issue.model.enumeration.IssueType;
import com.siewe_rostand.tvcam.Issue.model.enumeration.Priority;
import com.siewe_rostand.tvcam.Issue.repository.IssueRepository;
import com.siewe_rostand.tvcam.Users.models.Users;
import com.siewe_rostand.tvcam.Users.repository.UsersRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;

/**
 * Implémentation du service Issue
 *
 * @author rostand
 * @project tv-cam
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final CustomerService customerService;
    private final UsersRepository usersRepository;
    private final ZoneSecurityService zoneSecurityService;
    private final ObjectsValidator<IssueRequest> validator;
    private final IssueMapper issueMapper;

    @Override
    @Transactional
    public IssueResponse createIssue(IssueRequest request) {
        log.info("Création d'une nouvelle réclamation: {}", request.getTitle());
        validator.validate(request);

        // Vérifier que le client existe
        Customers customer = customerService.getById(request.getCustomerId());

        // Vérifier l'accès à la zone du client
        if (!zoneSecurityService.isAdmin() && customer.getZone() != null) {
            if (!zoneSecurityService.hasAccessToZone(customer.getZone().getZoneId())) {
                throw new RuntimeException("Accès refusé : vous n'avez pas accès à cette zone");
            }
        }

        Issue issue = issueMapper.toIssue(request);
        issue.setCustomer(customer);

        // Assigner automatiquement si un technicien est spécifié
        if (request.getAssignedToId() != null) {
            Users assignee = usersRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Utilisateur non trouvé avec l'ID: " + request.getAssignedToId()));
            issue.setAssignedTo(assignee);
        }

        Issue savedIssue = issueRepository.save(issue);
        log.info("Réclamation créée avec succès. Référence: {}", savedIssue.getReferenceNumber());

        return issueMapper.toResponse(savedIssue);
    }

    @Override
    @Transactional
    public IssueResponse updateIssue(Long issueId, IssueRequest request) {
        log.info("Mise à jour de la réclamation ID: {}", issueId);
        validator.validate(request);

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Réclamation non trouvée avec l'ID: " + issueId));

        // Vérifier l'accès
        checkZoneAccess(issue);

        // Mettre à jour les champs
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setIssueType(request.getIssueType());
        if (request.getPriority() != null) {
            issue.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            issue.setDueDate(request.getDueDate());
        }
        if (request.getNotes() != null) {
            issue.setNotes(request.getNotes());
        }

        Issue updatedIssue = issueRepository.save(issue);
        return issueMapper.toResponse(updatedIssue);
    }

    @Override
    @Transactional
    public HttpResponse<Object> deleteIssue(Long issueId) {
        log.info("Suppression de la réclamation ID: {}", issueId);

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Réclamation non trouvée avec l'ID: " + issueId));

        // Seuls les admins peuvent supprimer
        if (!zoneSecurityService.isAdmin()) {
            throw new RuntimeException("Seuls les administrateurs peuvent supprimer les réclamations");
        }

        issueRepository.delete(issue);

        return HttpResponse.builder()
                .message("Réclamation supprimée avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .timestamp(now()).success(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public IssueResponse getIssueById(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Réclamation non trouvée avec l'ID: " + issueId));

        checkZoneAccess(issue);
        return issueMapper.toResponse(issue);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse getAllIssues(Integer page, Integer size, String sortBy, String direction,
                                          String searchTerm, String status, String issueType, String priority) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);

        IssueStatus statusEnum = status != null ? IssueStatus.valueOf(status) : null;
        IssueType typeEnum = issueType != null ? IssueType.valueOf(issueType) : null;
        Priority priorityEnum = priority != null ? Priority.valueOf(priority) : null;

        Page<Issue> issuesPage;

        if (zoneSecurityService.isAdmin()) {
            // Admin voit toutes les réclamations
            issuesPage = issueRepository.findWithFilters(searchTerm, statusEnum, typeEnum, priorityEnum, pageable);
        } else {
            // Filtrer par zones de l'utilisateur
            List<Long> userZoneIds = zoneSecurityService.getCurrentUserZoneIds();
            issuesPage = issueRepository.findByCustomerZoneIds(userZoneIds, pageable);
        }

        List<IssueResponse> issueResponses = issuesPage.getContent().stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());

        return PaginatedResponse.builder()
                .data(issueResponses)
                .page(page)
                .totalElements((int) issuesPage.getTotalElements())
                .totalPages(issuesPage.getTotalPages())
                .firstPage(issuesPage.isFirst())
                .lastPage(issuesPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByCustomer(Long customerId) {
        Customers customer = customerService.getById(customerId);

        // Vérifier l'accès à la zone du client
        if (!zoneSecurityService.isAdmin() && customer.getZone() != null) {
            if (!zoneSecurityService.hasAccessToZone(customer.getZone().getZoneId())) {
                throw new RuntimeException("Accès refusé : vous n'avez pas accès à cette zone");
            }
        }

        List<Issue> issues = issueRepository.findByCustomer(customer);
        return issues.stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByAssignee(Long userId) {
        List<Issue> issues = issueRepository.findByAssignedToUserId(userId);
        return issues.stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HttpResponse<Object> assignIssue(Long issueId, Long userId) {
        log.info("Assignation de la réclamation {} à l'utilisateur {}", issueId, userId);

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Réclamation non trouvée avec l'ID: " + issueId));

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));

        checkZoneAccess(issue);

        issue.setAssignedTo(user);
        if (issue.getStatus() == IssueStatus.OPEN) {
            issue.setStatus(IssueStatus.IN_PROGRESS);
        }

        issueRepository.save(issue);

        return HttpResponse.builder()
                .message("Réclamation assignée avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .timestamp(now()).success(true)
                .build();
    }

    @Override
    @Transactional
    public HttpResponse<Object> resolveIssue(Long issueId, String resolution) {
        log.info("Résolution de la réclamation ID: {}", issueId);

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Réclamation non trouvée avec l'ID: " + issueId));

        checkZoneAccess(issue);

        issue.setStatus(IssueStatus.RESOLVED);
        issue.setResolution(resolution);
        issue.setResolvedAt(now());

        issueRepository.save(issue);

        return HttpResponse.builder().success(true)
                .message("Réclamation résolue avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .timestamp(now())
                .build();
    }

    @Override
    @Transactional
    public HttpResponse<Object> closeIssue(Long issueId) {
        log.info("Fermeture de la réclamation ID: {}", issueId);

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Réclamation non trouvée avec l'ID: " + issueId));

        checkZoneAccess(issue);

        issue.setStatus(IssueStatus.CLOSED);
        issueRepository.save(issue);

        return HttpResponse.builder().success(true)
                .message("Réclamation fermée avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .timestamp(now())
                .build();
    }

    @Override
    @Transactional
    public HttpResponse<Object> provideFeedback(Long issueId, Integer rating, String feedback) {
        log.info("Ajout de feedback pour la réclamation ID: {}", issueId);

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Réclamation non trouvée avec l'ID: " + issueId));

        if (issue.getStatus() != IssueStatus.RESOLVED && issue.getStatus() != IssueStatus.CLOSED) {
            throw new RuntimeException("Le feedback ne peut être ajouté qu'aux réclamations résolues ou fermées");
        }

        issue.setCustomerRating(rating);
        issue.setCustomerFeedback(feedback);
        issueRepository.save(issue);

        return HttpResponse.builder().success(true)
                .message("Feedback ajouté avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .timestamp(now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getIssueStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalIssues", issueRepository.count());
        stats.put("openIssues", issueRepository.countByStatus(IssueStatus.OPEN));
        stats.put("inProgressIssues", issueRepository.countByStatus(IssueStatus.IN_PROGRESS));
        stats.put("resolvedIssues", issueRepository.countByStatus(IssueStatus.RESOLVED));
        stats.put("closedIssues", issueRepository.countByStatus(IssueStatus.CLOSED));

        // Statistiques par type
        Map<String, Long> issuesByType = new HashMap<>();
        for (IssueType type : IssueType.values()) {
            long count = issueRepository.findByIssueType(type).size();
            issuesByType.put(type.name(), count);
        }
        stats.put("issuesByType", issuesByType);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getOverdueIssues() {
        List<Issue> overdueIssues = issueRepository.findOverdueIssues(now());
        return overdueIssues.stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByZone(Long zoneId) {
        if (!zoneSecurityService.hasAccessToZone(zoneId) && !zoneSecurityService.isAdmin()) {
            throw new RuntimeException("Accès refusé : vous n'avez pas accès à cette zone");
        }

        Page<Issue> issues = issueRepository.findByCustomerZoneIds(List.of(zoneId),
                PageRequest.of(0, Integer.MAX_VALUE));

        return issues.getContent().stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Vérifie l'accès à une réclamation basé sur la zone du client
     */
    private void checkZoneAccess(Issue issue) {
        if (!zoneSecurityService.isAdmin() && issue.getCustomer().getZone() != null) {
            if (!zoneSecurityService.hasAccessToZone(issue.getCustomer().getZone().getZoneId())) {
                throw new RuntimeException("Accès refusé : vous n'avez pas accès à cette zone");
            }
        }
    }
}
