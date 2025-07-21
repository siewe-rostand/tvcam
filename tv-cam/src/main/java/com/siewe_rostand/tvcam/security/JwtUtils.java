package com.siewe_rostand.tvcam.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.siewe_rostand.tvcam.common.exceptions.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/**
 * @author rostand
 * @project tvcam
 */
public class JwtUtils {
  public static String getJwtFromRequest(HttpServletRequest request)
      throws JwtAuthenticationException {
    String authHeader = request.getHeader(AUTHORIZATION);
    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    throw new JwtAuthenticationException(
        "No Bearer in Authorization", "No JWT token found in request headers");
  }
}
