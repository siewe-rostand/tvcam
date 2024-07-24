package com.siewe_rostand.tvcam.security;

import com.siewe_rostand.tvcam.exceptions.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author rostand
 * @project tv-cam
 */
@Service
public class JwtService {


    private final String secretKey;

    public JwtService(@Value("${security.jwt.security-key}") String secretKey){
        this.secretKey = secretKey;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws JwtAuthenticationException {
        try{
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("Error parsing JWT token: " + e.getMessage());
        }
    }

    private Claims extractAllClaims(String token)   throws JwtAuthenticationException {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (SecurityException ex) {
            throw new JwtAuthenticationException("Invalid JWT signature\n" + ex.getMessage());
        } catch (MalformedJwtException ex) {
            throw new JwtAuthenticationException("Invalid JWT token\n" + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            throw new JwtAuthenticationException("Expired JWT token\n" + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            throw new JwtAuthenticationException("Unsupported JWT token\n" + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new JwtAuthenticationException("JWT claims string is empty\n" + ex.getMessage());
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

    public String generateToken(UserDetails user) {
        return generateToken(user, new HashMap<>());
    }

    public String generateToken(UserDetails user, Map<String, Object> claims) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact()
                ;
    }
}
