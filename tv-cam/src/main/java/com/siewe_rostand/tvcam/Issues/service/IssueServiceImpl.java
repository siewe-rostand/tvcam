package com.siewe_rostand.tvcam.Issues.service;

import com.siewe_rostand.tvcam.Customers.Customers;
import com.siewe_rostand.tvcam.Customers.CustomersRepository;
import com.siewe_rostand.tvcam.Issues.dto.*;
import com.siewe_rostand.tvcam.Issues.model.Issue;
import com.siewe_rostand.tvcam.Issues.model.IssueComment;
import com.siewe_rostand.tvcam.Issues.model.IssueType;
import com.siewe_rostand.tvcam.Issues.model.enumeration.CommentType;
import com.siewe_rostand.tvcam.Issues.model.enumeration.IssueStatus;
import com.siewe_rostand.tvcam.Issues.repository.IssueCommentRepository;
import com.siewe_rostand.tvcam.Issues.repository.IssueRepository;
import com.siewe_rostand.tvcam.Issues.repository.IssueTypeRepository;
import com.siewe_rostand.tvcam.Users.Users;
import com.siewe_rostand.tvcam.Users.UsersRepository;
import com.siewe_rostand.tvcam.Zones.model.Zone;
import com.siewe_rostand.tvcam.Zones.repository.UserZoneRepository;
import com.siewe_rostand.tvcam.Zones.repository.ZoneRepository;
import com.siewe_rostand.tvcam.exceptions.ApiException;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of IssueService
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
    private final IssueCommentRepository issueCommentRepository;
    private final IssueTypeRepository issueTypeRepository;
    private final CustomersRepository customersRepository;
    private final UsersRepository usersRepository;
    private final ZoneRepository zoneRepository;
    private final UserZoneRepository userZoneRepository;
    private final IssueReferenceGenerator referenceGenerator;
    private final IssueMapper issueMapper;

    @Override
    public IssueResponse createIssue(IssueRequest request) {
        log.info("Creating new issue for customer: {}", request.getCustomerId());

        Customers customer = customersRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ApiException("Customer not found with ID: " + request.getCustomerId()));

        IssueType issueType = issueTypeRepository.findById(request.getIssueTypeId())
                .orElseThrow(() -> new ApiException("Issue type not found with ID: " + request.getIssueTypeId()));

        if (customer.getZone() == null) {
            throw new ApiException("Customer must be assigned to a zone before creating an issue");
        }

        Issue issue = issueMapper.toIssue(request);
        issue.setCustomer(customer);
        issue.setIssueType(issueType);
        issue.setZone(customer.getZone());

        // Generate unique reference
        String reference;
        do {
            reference = referenceGenerator.generateReference();
        } while (issueRepository.existsByReference(reference));
        issue.setReference(reference);

        // Set expected resolution date based on issue type if not provided
        if (issue.getExpectedResolutionDate() == null && issueType.getEstimatedResolutionTime() != null) {
            issue.setExpectedResolutionDate(
                issue.getReportedDate().plusHours(issueType.getEstimatedResolutionTime())
            );
        }

        // Assign technician if provided
        if (request.getAssignedTechnicianId() != null) {
            Users technician = usersRepository.findById(request.getAssignedTechnicianId())
                    .orElseThrow(() -> new ApiException("Technician not found with ID: " + request.getAssignedTechnicianId()));
            issue.setAssignedTechnician(technician);
        }

        Issue savedIssue = issueRepository.save(issue);
        log.info("Issue created successfully with reference: {}", savedIssue.getReference());

        return issueMapper.toResponse(savedIssue);
    }

    @Override
    public IssueResponse updateIssue(Long issueId, IssueRequest request) {
        log.info("Updating issue with ID: {}", issueId);

        Issue existingIssue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ApiException("Issue not found with ID: " + issueId));

        existingIssue.setTitle(request.getTitle());
        existingIssue.setDescription(request.getDescription());
        existingIssue.setLocationDetails(request.getLocationDetails());
        
        if (request.getExpectedResolutionDate() != null) {
            existingIssue.setExpectedResolutionDate(request.getExpectedResolutionDate());
        }

        // Update issue type if provided
        if (request.getIssueTypeId() != null && 
            !request.getIssueTypeId().equals(existingIssue.getIssueType().getIssueTypeId())) {
            IssueType issueType = issueTypeRepository.findById(request.getIssueTypeId())
                    .orElseThrow(() -> new ApiException("Issue type not found with ID: " + request.getIssueTypeId()));
            existingIssue.setIssueType(issueType);
        }

        Issue updatedIssue = issueRepository.save(existingIssue);
        log.info("Issue updated successfully with ID: {}", updatedIssue.getIssueId());

        return issueMapper.toResponse(updatedIssue);
    }

    @Override
    @Transactional(readOnly = true)
    public IssueResponse getIssueById(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ApiException("Issue not found with ID: " + issueId));
        return issueMapper.toResponse(issue);
    }

    @Override
    @Transactional(readOnly = true)
    public IssueResponse getIssueByReference(String reference) {
        Issue issue = issueRepository.findByReference(reference)
                .orElseThrow(() -> new ApiException("Issue not found with reference: " + reference));
        return issueMapper.toResponse(issue);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse getAllIssues(Integer page, Integer size, String sortBy, String direction, String search) {
        log.info("Fetching all issues with page: {}, size: {}, search: {}", page, size, search);
        
        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<Issue> issuePage;
        
        if (search != null && !search.trim().isEmpty()) {
            issuePage = issueRepository.findAllWithSearch(search.trim(), pageable);
        } else {
            issuePage = issueRepository.findAll(pageable);
        }
        
        return createPaginatedResponse(issuePage, "Issues retrieved successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse getIssuesByZone(Long zoneId, Integer page, Integer size, String sortBy, String direction, String search) {
        log.info("Fetching issues for zone: {}", zoneId);
        
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ApiException("Zone not found with ID: " + zoneId));
        
        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<Issue> issuePage;
        
        List<Zone> zones = Arrays.asList(zone);
        
        if (search != null && !search.trim().isEmpty()) {
            issuePage = issueRepository.findAllByZoneInWithSearch(zones, search.trim(), pageable);
        } else {
            issuePage = issueRepository.findAllByZoneIn(zones, pageable);
        }
        
        return createPaginatedResponse(issuePage, "Zone issues retrieved successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse getIssuesForUser(Long userId, Integer page, Integer size, String sortBy, String direction, String search) {
        log.info("Fetching issues for user: {}", userId);
        
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found with ID: " + userId));
        
        List<Zone> userZones = userZoneRepository.findZonesByUser(user);
        if (userZones.isEmpty()) {
            throw new ApiException("User is not assigned to any zones");
        }
        
        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<Issue> issuePage;
        
        if (search != null && !search.trim().isEmpty()) {
            issuePage = issueRepository.findAllByZoneInWithSearch(userZones, search.trim(), pageable);
        } else {
            issuePage = issueRepository.findAllByZoneIn(userZones, pageable);
        }
        
        return createPaginatedResponse(issuePage, "User zone issues retrieved successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getCustomerIssues(Long customerId) {
        Customers customer = customersRepository.findById(customerId)
                .orElseThrow(() -> new ApiException("Customer not found with ID: " + customerId));
        
        List<Issue> issues = issueRepository.findAllByCustomer(customer);
        return issues.stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getTechnicianAssignedIssues(Long technicianId) {
        Users technician = usersRepository.findById(technicianId)
                .orElseThrow(() -> new ApiException("Technician not found with ID: " + technicianId));
        
        List<Issue> issues = issueRepository.findAllByAssignedTechnician(technician);
        return issues.stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public IssueResponse updateIssueStatus(IssueStatusUpdateRequest request) {
        log.info("Updating status for issue: {}", request.getIssueId());
        
        Issue issue = issueRepository.findById(request.getIssueId())
                .orElseThrow(() -> new ApiException("Issue not found with ID: " + request.getIssueId()));
        
        IssueStatus newStatus;
        try {
            newStatus = IssueStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid status: " + request.getStatus());
        }
        
        issue.setStatus(newStatus);
        
        if (request.getResolutionNotes() != null) {
            issue.setResolutionNotes(request.getResolutionNotes());
        }
        
        if (request.getActualResolutionDate() != null) {
            issue.setActualResolutionDate(request.getActualResolutionDate());
        } else if (newStatus == IssueStatus.RESOLVED || newStatus == IssueStatus.CLOSED) {
            issue.setActualResolutionDate(LocalDateTime.now());
        }
        
        if (request.getCustomerSatisfactionRating() != null) {
            issue.setCustomerSatisfactionRating(request.getCustomerSatisfactionRating());
        }
        
        Issue updatedIssue = issueRepository.save(issue);
        
        // Add status update comment if provided
        if (request.getStatusComment() != null && !request.getStatusComment().trim().isEmpty()) {
            IssueComment statusComment = IssueComment.builder()
                    .issue(updatedIssue)
                    .comment(request.getStatusComment())
                    .commentType(CommentType.STATUS_UPDATE)
                    .isInternal(true)
                    .build();
            issueCommentRepository.save(statusComment);
        }
        
        log.info("Issue status updated successfully to: {}", newStatus);
        return issueMapper.toResponse(updatedIssue);
    }

    @Override
    public IssueResponse assignTechnician(Long issueId, Long technicianId) {
        log.info("Assigning technician {} to issue {}", technicianId, issueId);
        
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ApiException("Issue not found with ID: " + issueId));
        
        Users technician = usersRepository.findById(technicianId)
                .orElseThrow(() -> new ApiException("Technician not found with ID: " + technicianId));
        
        // Verify technician has access to the issue's zone
        if (!userZoneRepository.existsByUserAndZoneAndIsActiveTrue(technician, issue.getZone())) {
            throw new ApiException("Technician does not have access to the issue's zone");
        }
        
        issue.setAssignedTechnician(technician);
        
        // Update status to IN_PROGRESS if it's OPEN
        if (issue.getStatus() == IssueStatus.OPEN) {
            issue.setStatus(IssueStatus.IN_PROGRESS);
        }
        
        Issue updatedIssue = issueRepository.save(issue);
        
        // Add assignment comment
        IssueComment assignmentComment = IssueComment.builder()
                .issue(updatedIssue)
                .comment("Issue assigned to technician: " + technician.getFirstname() + " " + technician.getLastname())
                .commentType(CommentType.STATUS_UPDATE)
                .isInternal(true)
                .build();
        issueCommentRepository.save(assignmentComment);
        
        log.info("Technician assigned successfully");
        return issueMapper.toResponse(updatedIssue);
    }

    @Override
    public void escalateIssue(Long issueId, String escalationReason) {
        log.info("Escalating issue: {}", issueId);
        
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ApiException("Issue not found with ID: " + issueId));
        
        // Set priority to HIGH or URGENT based on current priority
        if (issue.getPriority().ordinal() < issue.getPriority().values().length - 1) {
            issue.setPriority(issue.getPriority().values()[issue.getPriority().ordinal() + 1]);
        }
        
        issueRepository.save(issue);
        
        // Add escalation comment
        IssueComment escalationComment = IssueComment.builder()
                .issue(issue)
                .comment("Issue escalated. Reason: " + escalationReason)
                .commentType(CommentType.ESCALATION)
                .isInternal(true)
                .build();
        issueCommentRepository.save(escalationComment);
        
        log.info("Issue escalated successfully");
    }

    @Override
    public IssueCommentResponse addComment(IssueCommentRequest request) {
        log.info("Adding comment to issue: {}", request.getIssueId());
        
        Issue issue = issueRepository.findById(request.getIssueId())
                .orElseThrow(() -> new ApiException("Issue not found with ID: " + request.getIssueId()));
        
        IssueComment comment = issueMapper.toIssueComment(request);
        comment.setIssue(issue);
        
        IssueComment savedComment = issueCommentRepository.save(comment);
        log.info("Comment added successfully");
        
        return issueMapper.toCommentResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse getIssueComments(Long issueId, Integer page, Integer size, Boolean includeInternal) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ApiException("Issue not found with ID: " + issueId));
        
        Pageable pageable = createPageable(page, size, "createdAt", "desc");
        Page<IssueComment> commentPage = issueCommentRepository.findCommentsByIssue(
                issue, includeInternal != null ? includeInternal : false, pageable);
        
        List<IssueCommentResponse> comments = commentPage.getContent().stream()
                .map(issueMapper::toCommentResponse)
                .collect(Collectors.toList());
        
        return PaginatedResponse.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Issue comments retrieved successfully")
                .data(comments)
                .lastPage(commentPage.isLast())
                .firstPage(commentPage.isFirst())
                .empty(commentPage.isEmpty())
                .sorted(commentPage.getSort().isSorted())
                .totalPages(commentPage.getTotalPages())
                .totalElements((int) commentPage.getTotalElements())
                .numberOfElements(commentPage.getNumberOfElements())
                .page(commentPage.getNumber())
                .paged(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public IssueStatsResponse getIssueStats(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found with ID: " + userId));
        
        List<Zone> userZones = userZoneRepository.findZonesByUser(user);
        
        return IssueStatsResponse.builder()
                .totalIssues(issueRepository.countByZoneInAndStatus(userZones, null))
                .openIssues(issueRepository.countByZoneInAndStatus(userZones, IssueStatus.OPEN))
                .inProgressIssues(issueRepository.countByZoneInAndStatus(userZones, IssueStatus.IN_PROGRESS))
                .resolvedIssues(issueRepository.countByZoneInAndStatus(userZones, IssueStatus.RESOLVED))
                .closedIssues(issueRepository.countByZoneInAndStatus(userZones, IssueStatus.CLOSED))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public IssueStatsResponse getZoneIssueStats(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ApiException("Zone not found with ID: " + zoneId));
        
        List<Zone> zones = Arrays.asList(zone);
        
        return IssueStatsResponse.builder()
                .totalIssues(issueRepository.countByZoneInAndStatus(zones, null))
                .openIssues(issueRepository.countByZoneInAndStatus(zones, IssueStatus.OPEN))
                .inProgressIssues(issueRepository.countByZoneInAndStatus(zones, IssueStatus.IN_PROGRESS))
                .resolvedIssues(issueRepository.countByZoneInAndStatus(zones, IssueStatus.RESOLVED))
                .closedIssues(issueRepository.countByZoneInAndStatus(zones, IssueStatus.CLOSED))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getOverdueIssues() {
        List<IssueStatus> excludedStatuses = Arrays.asList(IssueStatus.RESOLVED, IssueStatus.CLOSED, IssueStatus.CANCELLED);
        List<Issue> overdueIssues = issueRepository.findOverdueIssues(LocalDateTime.now(), excludedStatuses);
        
        return overdueIssues.stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    private Pageable createPageable(Integer page, Integer size, String sortBy, String direction) {
        page = (page != null && page >= 0) ? page : 0;
        size = (size != null && size > 0) ? size : 10;
        sortBy = (sortBy != null && !sortBy.trim().isEmpty()) ? sortBy : "reportedDate";
        
        Sort.Direction sortDirection = Sort.Direction.DESC;
        if ("asc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.ASC;
        }
        
        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }

    private PaginatedResponse createPaginatedResponse(Page<Issue> issuePage, String message) {
        List<IssueResponse> issues = issuePage.getContent().stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
        
        return PaginatedResponse.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message(message)
                .data(issues)
                .lastPage(issuePage.isLast())
                .firstPage(issuePage.isFirst())
                .empty(issuePage.isEmpty())
                .sorted(issuePage.getSort().isSorted())
                .totalPages(issuePage.getTotalPages())
                .totalElements((int) issuePage.getTotalElements())
                .numberOfElements(issuePage.getNumberOfElements())
                .page(issuePage.getNumber())
                .paged(true)
                .build();
    }
}