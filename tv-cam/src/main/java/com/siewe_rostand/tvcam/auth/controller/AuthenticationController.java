package com.siewe_rostand.tvcam.auth.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siewe_rostand.tvcam.auth.dto.AuthenticationRequest;
import com.siewe_rostand.tvcam.auth.dto.AuthenticationResponse;
import com.siewe_rostand.tvcam.auth.dto.ForgetPasswordForm;
import com.siewe_rostand.tvcam.auth.dto.RegisterRequest;
import com.siewe_rostand.tvcam.auth.services.AuthenticationService;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author rostand
 * @project tv-cam
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService service;


    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(
            @RequestBody RegisterRequest request
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        var response = service.register(request);
        Map<String, Object> data = objectMapper
                .convertValue(response, new TypeReference<>() {
                });
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(HttpStatus.CREATED).statusCode(HttpStatus.CREATED.value())
                        .message("User successfully created")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/register1")
    @ResponseBody
    public ResponseEntity<AuthenticationResponse> register1(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Sign in user to the app", description = "Login user to get tokens")
    @ApiResponse(description = "Login end point", responseCode = "200")
    @ApiResponse(responseCode = "400", description = "password and/or telephone number is/are incorrect")
    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @Operation(summary = "change password", description = "change the user password")
    @PostMapping("/password/change")
    public ResponseEntity<HttpResponse> forgotPassword(@RequestBody ForgetPasswordForm forgetPasswordForm) {
        return ResponseEntity.ok(service.forgottenPassword(forgetPasswordForm));
    }

}
