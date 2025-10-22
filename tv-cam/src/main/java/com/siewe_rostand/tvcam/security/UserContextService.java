package com.siewe_rostand.tvcam.security;

import com.siewe_rostand.tvcam.Users.models.Users;
import com.siewe_rostand.tvcam.Users.repository.UsersRepository;
import com.siewe_rostand.tvcam.common.exceptions.ApiException;
import com.siewe_rostand.tvcam.common.exceptions.JwtAuthenticationException;
import com.siewe_rostand.tvcam.shared.Exceptions.UnAuthorizeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * Service to get the currently authenticated user
 *
 * @author rostand
 * @project tv-cam
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserContextService {

    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Get the currently authenticated user - Primary method that uses multiple strategies
     * This method tries different approaches to get the current user:
     * 1. Direct JWT extraction from current request (most reliable)
     * 2. Fall back to Spring Security context
     *
     * @return Optional<Users> - the authenticated user or empty if not authenticated
     */
    public Optional<Users> getCurrentUser() {
        log.debug("Getting current user using multiple strategies");

        // Strategy 1: Try to get user from current HTTP request JWT (like /auth/user endpoint)
        try {
            Optional<Users> userFromRequest = getCurrentUserFromCurrentRequest();
            if (userFromRequest.isPresent()) {
                log.debug("Successfully retrieved user from current request JWT");
                return userFromRequest;
            }
        } catch (Exception e) {
            log.debug("Failed to get user from current request: {}", e.getMessage());
        }

        // Strategy 2: Fall back to Spring Security context
        log.debug("Falling back to Spring Security context");
        return getCurrentUserFromSecurityContext();
    }

    /**
     * Get current user from the current HTTP request (like the working /auth/user endpoint)
     * This uses the same approach as AuthenticationService.getUserInfo()
     *
     * @return Optional<Users> - the authenticated user or empty if not found
     */
    private Optional<Users> getCurrentUserFromCurrentRequest() {
        try {
            // Get current request from RequestContextHolder
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                log.debug("No request attributes available");
                return Optional.empty();
            }

            HttpServletRequest request = attributes.getRequest();
            return getCurrentUserFromRequest(request);

        } catch (Exception e) {
            log.debug("Error getting user from current request: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get the currently authenticated user from Spring Security context
     *
     * @return Optional<Users> - the authenticated user or empty if not authenticated
     */
    private Optional<Users> getCurrentUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.debug("Current authentication: {}", authentication);
        log.debug("Authentication type: {}",
                authentication != null ? authentication.getClass().getSimpleName() : "null");

        // Check if authentication is null, not authenticated, or anonymous
        if (authentication == null) {
            log.warn("Authentication is null - user not authenticated");
            return Optional.empty();
        }

        if (authentication instanceof AnonymousAuthenticationToken) {
            log.warn("Anonymous authentication detected - user not authenticated");
            return Optional.empty();
        }

        if (!authentication.isAuthenticated()) {
            log.warn("Authentication is not authenticated");
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        log.debug("Principal type: {}, Principal: {}",
                principal.getClass().getSimpleName(), principal);

        // Extract user details from authenticated principal
        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            log.debug("Attempting to load user by telephone: {}", username);

            try {
                Optional<Users> user = usersRepository.getByTelephone(username);

                if (user.isPresent()) {
                    log.debug("User found: {} (ID: {})", username, user.get().getUserId());
                    return user;
                } else {
                    log.warn("User not found in database for telephone: {}", username);
                    return Optional.empty();
                }
            } catch (Exception e) {
                log.error("Error while getting user by telephone: {}", username, e);
                throw new ApiException(
                        "Failed to retrieve user from database",
                        "An error occurred while processing your authentication: " + e.getMessage()
                );
            }
        }

        // Unexpected principal type
        log.warn("Unexpected principal type: {}. Expected UserDetails", principal.getClass().getSimpleName());
        return Optional.empty();
    }

    /**
     * Get the currently authenticated user, throwing exception if not found
     *
     * @return Users - the authenticated user
     * @throws UnAuthorizeException if user is not authenticated or not found
     */
    public Users getCurrentUserOrThrow() throws UnAuthorizeException {
        return getCurrentUser()
                .orElseThrow(() -> {
                    log.error("No authenticated user found using any strategy");
                    return new UnAuthorizeException(
                            "User must be authenticated to access this resource. Please login first and provide a valid JWT token."
                    );
                });
    }

    /**
     * Get the currently authenticated user from JWT token in request
     * Uses the same logic as the working /auth/user endpoint
     *
     * @param request HttpServletRequest containing the JWT token
     * @return Optional<Users> - the authenticated user or empty if not found
     */
    public Optional<Users> getCurrentUserFromRequest(HttpServletRequest request) {
        try {
            log.debug("Extracting JWT from request using same method as /auth/user endpoint");

            // Extract JWT token from request (same as getUserInfo method)
            String jwt = JwtUtils.getJwtFromRequest(request);
            log.debug("JWT token extracted successfully");

            // Extract username from JWT
            String username;
            try {
                username = jwtService.extractUsername(jwt);
                log.debug("Username extracted from JWT: {}", username);
            } catch (Exception e) {
                log.error("Invalid or expired access token: {}", e.getMessage());
                return Optional.empty();
            }

            if (username == null) {
                log.error("Username is null after JWT extraction");
                return Optional.empty();
            }

            // Load and validate user details (same as getUserInfo method)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails == null || !jwtService.isTokenValid(jwt, userDetails)) {
                log.error("JWT token validation failed for user: {}", username);
                return Optional.empty();
            }

            // Find user in database
            Users user = usersRepository.findByTelephone(username);
            if (user == null) {
                log.warn("User not found in database for telephone: {}", username);
                return Optional.empty();
            }

            log.debug("User found from JWT: {} (ID: {})", username, user.getUserId());
            return Optional.of(user);

        } catch (JwtAuthenticationException e) {
            log.error("JWT authentication error: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error while extracting user from request", e);
            return Optional.empty();
        }
    }

    /**
     * Get the currently authenticated user from JWT token in request, throwing exception if not found
     *
     * @param request HttpServletRequest containing the JWT token
     * @return Users - the authenticated user
     * @throws JwtAuthenticationException if user is not authenticated or not found
     */
    public Users getCurrentUserFromRequestOrThrow(HttpServletRequest request) throws JwtAuthenticationException {
        return getCurrentUserFromRequest(request)
                .orElseThrow(() -> {
                    log.error("No authenticated user found in request JWT");
                    return new JwtAuthenticationException(
                            "No authenticated user found in request",
                            "Valid JWT token required to access this resource"
                    );
                });
    }

    /**
     * Check if there is a currently authenticated user
     *
     * @return boolean - true if user is authenticated, false otherwise
     */
    public boolean isUserAuthenticated() {
        boolean isAuthenticated = getCurrentUser().isPresent();
        log.debug("User authentication status: {}", isAuthenticated);
        return isAuthenticated;
    }

    /**
     * Get the username (telephone) of the currently authenticated user
     *
     * @return Optional<String> - the username or empty if not authenticated
     */
    public Optional<String> getCurrentUsername() {
        return getCurrentUser().map(Users::getUsername);
    }

    /**
     * Get the full name of the currently authenticated user
     *
     * @return Optional<String> - the full name or empty if not authenticated
     */
    public Optional<String> getCurrentUserFullName() {
        return getCurrentUser().map(Users::getFullName);
    }

    /**
     * Get the user ID of the currently authenticated user
     *
     * @return Optional<Long> - the user ID or empty if not authenticated
     */
    public Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(Users::getUserId);
    }
}
