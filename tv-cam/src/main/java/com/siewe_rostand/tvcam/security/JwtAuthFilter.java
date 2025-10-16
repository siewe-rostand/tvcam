package com.siewe_rostand.tvcam.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siewe_rostand.tvcam.common.exceptions.JwtAuthenticationException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.siewe_rostand.tvcam.security.JwtUtils.getJwtFromRequest;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author rostand
 * @project tv-cam
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String path = request.getServletPath();
            if (path.contains("/auth")
                    || path.contains("/swagger-ui")
                    || path.contains("/v3/api-docs")
                    || path.contains("/swagger-resources")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt;
            final String userEmail;

            jwt = getJwtFromRequest(request);
            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    throw new JwtAuthenticationException(
                            "Access Token invalid because your have been logout:: reconnect please",
                            "Logout From System");
                }
            }
            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            handleAuthenticationException(response, e);
        }
    }

    private void handleAuthenticationException(
            HttpServletResponse response, JwtAuthenticationException exp) throws IOException {
        response.setStatus(FORBIDDEN.value());
        response.setContentType(APPLICATION_JSON_VALUE);

        HttpResponse<Object> errorResponse =
                HttpResponse.builder().timestamp(LocalDateTime.now()).success(false)
                        .message(exp.getMessage())
                        .reason(
                                exp.reason != null
                                        ? exp.reason
                                        : "Check that you set \"Bearer\" in Authorization Header")
                        .errorCause(exp.getCause())
                        .status(FORBIDDEN.getReasonPhrase())
                        .statusCode(FORBIDDEN.value())
                        .build();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}
