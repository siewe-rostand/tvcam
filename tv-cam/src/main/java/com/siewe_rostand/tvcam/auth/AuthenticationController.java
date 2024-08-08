package com.siewe_rostand.tvcam.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siewe_rostand.tvcam.shared.HttpResponse;
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
//@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService service;


    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(
            @RequestBody RegisterRequest request
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        var response = service.register(request);
        Map<String, Object> data = objectMapper
                .convertValue(response, new TypeReference<Map<String, Object>>() {});
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

    @PostMapping("/authenticate")
    public ResponseEntity<HttpResponse> login(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
