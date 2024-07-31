package com.siewe_rostand.tvcam.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siewe_rostand.tvcam.exceptions.JwtAuthenticationException;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
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

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
       try {
           if (request.getServletPath().contains("/api/v1/auth")) {
               filterChain.doFilter(request, response);
               return;
           }

           final String jwt;
           final String userEmail;

//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            log.info("No bearer token found");
//            filterChain.doFilter(request, response);
//            return;
//        }

           jwt = getJwtFromRequest(request);
           userEmail = jwtService.extractUsername(jwt);

           if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
               UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
               if (jwtService.isTokenValid(jwt, userDetails)) {
                   UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                           null, userDetails.getAuthorities());
                   authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                   SecurityContextHolder.getContext().setAuthentication(authToken);
               }
           }
       }catch (JwtAuthenticationException e) {
           SecurityContextHolder.clearContext();
           handleAuthenticationException(response, e);
           return;
       }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request)  throws JwtAuthenticationException {
        String authHeader = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new JwtAuthenticationException("No Bearer in Authorization", "No JWT token found in request headers");
    }

    private void handleAuthenticationException(HttpServletResponse response, JwtAuthenticationException e)
            throws IOException {
        response.setStatus(UNAUTHORIZED.value());
        response.setContentType(APPLICATION_JSON_VALUE);

        HttpResponse errorResponse = HttpResponse.builder().message("Check that you set \"Bearer\" in Authorization Header").developerMessage(e.getMessage())
                .status(UNAUTHORIZED).statusCode(UNAUTHORIZED.value()).build();
        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}
