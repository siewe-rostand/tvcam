package com.siewe_rostand.tvcam.auth.controller;

import com.siewe_rostand.tvcam.Users.dto.UserMapper;
import com.siewe_rostand.tvcam.Users.dto.UserResponse;
import com.siewe_rostand.tvcam.Users.models.Users;
import com.siewe_rostand.tvcam.auth.dto.AuthenticationRequest;
import com.siewe_rostand.tvcam.auth.dto.AuthenticationResponse;
import com.siewe_rostand.tvcam.auth.dto.ForgetPasswordForm;
import com.siewe_rostand.tvcam.auth.dto.RegisterRequest;
import com.siewe_rostand.tvcam.auth.services.AuthenticationService;
import com.siewe_rostand.tvcam.security.UserContextService;
import com.siewe_rostand.tvcam.shared.Exceptions.UnAuthorizeException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * @author rostand
 * @project tv-cam
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService service;
    private final UserContextService userContextService;
    private final UserMapper mapper;

    @PostMapping("/register")
    public ResponseEntity<HttpResponse<AuthenticationResponse>> register(
            @RequestBody RegisterRequest request) {
        AuthenticationResponse response = service.register(request);
        return ResponseEntity.ok().body(
                HttpResponse.<AuthenticationResponse>builder().timestamp(now()).success(true)
                        .status(CREATED.getReasonPhrase()).statusCode(CREATED.value())
                        .message("User successfully created").data(response)
                        .build());
    }

    @PostMapping("/register1")
    public ResponseEntity<AuthenticationResponse> register1(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Sign in user to the app", description = "Login user to get tokens")
    @ApiResponse(description = "Login end point", responseCode = "200")
    @ApiResponse(responseCode = "400", description = "password and/or telephone number is/are incorrect")
    @PostMapping("/login")
    public ResponseEntity<HttpResponse<AuthenticationResponse>> login(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @Operation(summary = "change password", description = "change the user password")
    @PostMapping("/password/change")
    public ResponseEntity<HttpResponse<AuthenticationResponse>> forgotPassword(@RequestBody ForgetPasswordForm forgetPasswordForm) {
        return ResponseEntity.ok(service.forgottenPassword(forgetPasswordForm));
    }

    @GetMapping("/user")
    public ResponseEntity<HttpResponse<UserResponse>> getUserInfo(HttpServletRequest request) throws UnAuthorizeException {
        log.info("get user info {}", request);
        return ResponseEntity.ok().body(service.getUserInfo(request));
    }

    @Operation(summary = "Get current authenticated user", description = "Get the currently connected user from security context")
    @ApiResponse(description = "Current user information", responseCode = "200")
    @GetMapping("/current-user")
    public ResponseEntity<HttpResponse<UserResponse>> getCurrentUser() {
        Users currentUser = userContextService.getCurrentUserOrThrow();
        UserResponse data = mapper.toResponse(currentUser);
        return ResponseEntity.ok().body(
                HttpResponse.<UserResponse>builder()
                        .timestamp(now())
                        .success(true)
                        .status("OK")
                        .statusCode(200)
                        .message("Current user retrieved successfully")
                        .data(data)
                        .build()
        );

    }

}
