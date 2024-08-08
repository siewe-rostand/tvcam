package com.siewe_rostand.tvcam.shared.configuration;

import com.siewe_rostand.tvcam.Users.Users;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * @author rostand
 * @project tv-cam
 */


public class ApplicationAuditAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        Users userPrincipal = (Users) authentication.getPrincipal();

        return Optional.ofNullable(userPrincipal.getUserId());
    }
}
