package com.siewe_rostand.tvcam.auth.services;

import com.siewe_rostand.tvcam.Roles.Roles;
import com.siewe_rostand.tvcam.Roles.RolesRepository;
import com.siewe_rostand.tvcam.Users.dto.UserMapper;
import com.siewe_rostand.tvcam.Users.dto.UserResponse;
import com.siewe_rostand.tvcam.Users.models.Users;
import com.siewe_rostand.tvcam.Users.repository.UsersRepository;
import com.siewe_rostand.tvcam.auth.dto.AuthenticationRequest;
import com.siewe_rostand.tvcam.auth.dto.AuthenticationResponse;
import com.siewe_rostand.tvcam.auth.dto.ForgetPasswordForm;
import com.siewe_rostand.tvcam.auth.dto.RegisterRequest;
import com.siewe_rostand.tvcam.common.constraints.validator.ObjectsValidator;
import com.siewe_rostand.tvcam.common.exceptions.EmptyPasswordException;
import com.siewe_rostand.tvcam.common.exceptions.JwtAuthenticationException;
import com.siewe_rostand.tvcam.security.JwtService;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityAlreadyExistException;
import com.siewe_rostand.tvcam.shared.Exceptions.OperationNotPermittedException;
import com.siewe_rostand.tvcam.shared.Exceptions.UnAuthorizeException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.siewe_rostand.tvcam.Roles.RoleType.ROLE_USER;
import static com.siewe_rostand.tvcam.common.utils.ApplicationConstants.ACCESS_TOKEN_VALIDITY_SECONDS;
import static com.siewe_rostand.tvcam.security.JwtUtils.getJwtFromRequest;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author rostand
 * @project tv-cam
 */

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RolesRepository roleRepository;
    private final ObjectsValidator<RegisterRequest> validator;
    private final ObjectsValidator<ForgetPasswordForm> passwordFormObjectsValidator;
    private final UserDetailsService userDetailsService;
    private final UserMapper mapper;

    private static final Map<String, Object> EMPTY_CLAIMS = Collections.unmodifiableMap(new HashMap<>());


    private Map<String, Object> buildClaims(Users user) {
        Map<String, Object> claims = new HashMap<>(EMPTY_CLAIMS);
        claims.put("firstname", user.getFirstname());
        claims.put("lastname", user.getLastname());
        claims.put("active", user.getActive());
        claims.put("role", user.getRoles());
        return claims;
    }

    @Override
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
        Roles userRole = roleRepository.getByName(ROLE_USER.name())
                .orElse(
                        Roles.builder()
                                .name(ROLE_USER.name())
                                .build()
                );
        if (userRole.getRoleId() == null) {
            userRole = roleRepository.save(userRole);
        }
        var defaultUserRole = Set.of(userRole);
        user.setRoles(defaultUserRole);

        var savedUser = usersRepository.save(user);

        Map<String, Object> claims = buildClaims(savedUser);
        var jwtToken = jwtService.generateToken(claims, savedUser);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public HttpResponse<AuthenticationResponse> login(AuthenticationRequest request) {
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
                .orElseThrow(() -> new EntityNotFoundException("No User found with this telephone number. Please check your number well"));

        Map<String, Object> claims = buildClaims(user);
        String jwtToken = jwtService.generateToken(claims, user);
        AuthenticationResponse response = AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .tokenType("Bearer")
                .expiresIn(ACCESS_TOKEN_VALIDITY_SECONDS)
                .build();

        return HttpResponse.<AuthenticationResponse>builder()
                .timestamp(now()).success(true)
                .message("login successfully")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .data(response)
                .build();
    }

    @Override
    public HttpResponse<AuthenticationResponse> forgottenPassword(ForgetPasswordForm forgetPasswordForm) {
        System.out.println(forgetPasswordForm);
        log.trace(forgetPasswordForm.toString());
        passwordFormObjectsValidator.validate(forgetPasswordForm);
        Users users = usersRepository.findByTelephone(forgetPasswordForm.getTelephone());
        if (users == null) {
            throw new EntityNotFoundException("Aucun utilisateur n'a été trouvé avec ce numéro de téléphone. Veuillez vérifier votre numéro de téléphone");
        }
        usersRepository.changePassword(passwordEncoder.encode(forgetPasswordForm.getNewPassword()));
        return HttpResponse.<AuthenticationResponse>builder()
                .timestamp(now()).success(true)
                .message("Password changed successfully")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .build();
    }

    @Override
    public HttpResponse<UserResponse> getUserInfo(HttpServletRequest request) throws UnAuthorizeException {
        String jwt = getJwtFromRequest(request);

        String userEmail;
        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            throw new UnAuthorizeException("Invalid or expired access token. Checked if access token is present and it is valid");
        }

        if (userEmail == null) {
            throw new OperationNotPermittedException("An error occurred when trying to parse the token");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        if (userDetails == null || !jwtService.isTokenValid(jwt, userDetails)) {
            throw new JwtAuthenticationException(
                    "Access Token invalid or expired:: please reconnect",
                    "Logout From System");
        }

        Users user = usersRepository.findByTelephone(userEmail);
        if (user == null) {
            throw new EntityNotFoundException("No user found with the provided telephone number");
        }

        UserResponse userResponse = mapper.toResponse(user);
        return HttpResponse.<UserResponse>builder()
                .timestamp(now()).success(true)
                .message("User info retrieved successfully")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .data(userResponse)
                .build();
    }
}
