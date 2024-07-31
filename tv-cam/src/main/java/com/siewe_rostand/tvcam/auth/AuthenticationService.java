package com.siewe_rostand.tvcam.auth;

import com.siewe_rostand.tvcam.Roles.RoleType;
import com.siewe_rostand.tvcam.Roles.Roles;
import com.siewe_rostand.tvcam.Roles.RolesRepository;
import com.siewe_rostand.tvcam.Users.Users;
import com.siewe_rostand.tvcam.Users.UsersRepository;
import com.siewe_rostand.tvcam.exceptions.EmptyPasswordException;
import com.siewe_rostand.tvcam.security.JwtService;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityAlreadyExistException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.validator.ObjectsValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Map.of;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author rostand
 * @project tv-cam
 */

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RolesRepository roleRepository;
    private final ObjectsValidator<RegisterRequest> validator;

    private static final Map<String, Object> EMPTY_CLAIMS = Collections.unmodifiableMap(new HashMap<>());

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        validator.validate(request);
        if (usersRepository.findByTelephone(request.getTelephone()) != null) {
            throw new EntityAlreadyExistException("A user with the telephone number " + request.getTelephone() + " already exist");
        }
        Users user = Users.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .telephone(request.getTelephone())
                .password(
                        passwordEncoder.encode(request.getPassword())
                )
                .active(true)
                .build();
        // set roles
        Roles userRole = roleRepository.getByName(RoleType.ROLE_USER.name())
                .orElse(
                        Roles.builder()
                                .name(RoleType.ROLE_USER.name())
                                .build()
                );
        if (userRole.getRoleId() == null) {
            userRole = roleRepository.save(userRole);
        }
        var defaultUserRole = Set.of(userRole);
        user.setRoles(defaultUserRole);

        var savedUser = usersRepository.save(user);

        Map<String, Object> claims = buildClaims(user);
        var jwtToken = jwtService.generateToken(claims, savedUser);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .fullname(user.getFirstname() + " " + user.getLastname())
                .userId(savedUser.getUserId())
                .telephone(user.getTelephone())
                .build();
    }

    public HttpResponse authenticate(AuthenticationRequest request) {
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new EmptyPasswordException("Encoded password cannot be empty");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getTelephone(),
                        request.getPassword()
                )
        );
        Users user = usersRepository.getByTelephone(request.getTelephone())
                .orElseThrow();

        Map<String, Object> claims = buildClaims(user);
        String jwtToken = jwtService.generateToken(claims, user);

        return HttpResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("login successfully")
                .status(OK)
                .statusCode(OK.value())
                .data(of("user", user, "access_token",jwtToken))
                .build();
    }
    private Map<String, Object> buildClaims(Users user) {
        Map<String, Object> claims = new HashMap<>(EMPTY_CLAIMS);
        claims.put("firstname", user.getFirstname());
        claims.put("lastname", user.getLastname());
        claims.put("active", user.getActive());
        claims.put("role", user.getRoles());
        return claims;
    }
}
