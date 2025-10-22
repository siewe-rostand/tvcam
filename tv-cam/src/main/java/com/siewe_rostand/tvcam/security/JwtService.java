package com.siewe_rostand.tvcam.security;

import com.siewe_rostand.tvcam.common.exceptions.JwtAuthenticationException;
import com.siewe_rostand.tvcam.common.utils.ApplicationConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author rostand
 * @project tv-cam
 */
@Service
public class JwtService {


    private final String secretKey;

    public JwtService(@Value("${security.jwt.security-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Map<String, Object> extractAllInfo(String token) {
        return extractAllClaims(token);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws JwtAuthenticationException {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException(e.getMessage(), "Error parsing JWT token");
        }
    }

    private Claims extractAllClaims(String token) throws JwtAuthenticationException {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException ex) {
            throw new JwtAuthenticationException(ex.getMessage(), "Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            throw new JwtAuthenticationException(ex.getMessage(), "Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new JwtAuthenticationException(ex.getMessage(), ex.getCause(), "Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new JwtAuthenticationException(ex.getMessage(), "Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new JwtAuthenticationException(ex.getMessage(), "JWT claims string is empty");
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        var expirationDate = extractExpirationDate(token);
        return expirationDate.before(new Date());
    }

    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        var authorities = userDetails.getAuthorities()
                .stream().
                map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(
                        System.currentTimeMillis()
                                + ApplicationConstants.ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
                .claim("authorities", authorities)
                .setId(UUID.randomUUID().toString())
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String doGenerateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject("#refresh" + username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + ApplicationConstants.REFRESH_TOKEN_VALIDITY_SECONDS * 1000))
                .setId(UUID.randomUUID().toString())
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
