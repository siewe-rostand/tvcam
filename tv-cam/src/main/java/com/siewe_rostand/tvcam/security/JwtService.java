package com.siewe_rostand.tvcam.security;

import com.siewe_rostand.tvcam.exceptions.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
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

    private final long jwtExpiration;

    public JwtService(@Value("${security.jwt.security-key}") String secretKey, @Value("${security.jwt.expiration}") Long jwtExpiration){
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws JwtAuthenticationException {
        try{
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException(e.getMessage(),"Error parsing JWT token");
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
            throw new JwtAuthenticationException(ex.getMessage(),"Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            throw new JwtAuthenticationException(ex.getMessage(),"Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new JwtAuthenticationException(ex.getMessage(),"Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new JwtAuthenticationException(ex.getMessage(), "Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new JwtAuthenticationException(ex.getMessage(),"JWT claims string is empty");
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
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
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
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .claim("authorities", authorities)
                .signWith(getSignInKey())
                .compact();
    }
}
