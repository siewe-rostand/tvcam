package com.siewe_rostand.tvcam.Users.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.siewe_rostand.tvcam.Users.dto.UserRequest;
import com.siewe_rostand.tvcam.Users.dto.UserResponse;
import com.siewe_rostand.tvcam.Users.dto.UsersDto;
import com.siewe_rostand.tvcam.Users.models.Users;
import com.siewe_rostand.tvcam.Users.services.UserService;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityNotFoundException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<HttpResponse<UserResponse>> saveUser(@RequestBody UserRequest usersDto) {
        UserResponse dto = userService.create(usersDto);
        return ResponseEntity.created(URI.create(""))
                .body(
                        HttpResponse.<UserResponse>builder()
                                .timestamp(now()).success(true)
                                .message("User created successfully")
                                .data(dto)
                                .status(CREATED.getReasonPhrase())
                                .statusCode(CREATED.value())
                                .build());
    }

    @PutMapping("/edit")
    public ResponseEntity<HttpResponse<UsersDto>> updateUser(@RequestBody UsersDto usersDto) {
        UsersDto dto = new UsersDto().CreateDTO(userService.updateUser(usersDto));
        return ResponseEntity.ok()
                .body(
                        HttpResponse.<UsersDto>builder()
                                .timestamp(now()).success(true)
                                .message("User data updated successfully")
                                .data(dto)
                                .status(OK.getReasonPhrase())
                                .statusCode(OK.value())
                                .build());
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<UserResponse>> getAllUsers(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                       @RequestParam(name = "size", defaultValue = "999999") Integer size,
                                                                       @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                       @RequestParam(name = "direction", defaultValue = "desc") String direction,
                                                                       @RequestParam(name = "name", defaultValue = "") String name) {
        PaginatedResponse<UserResponse> response = userService.findAll(page, size, sortBy, direction, name);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/search")
    public Map<String, List<UsersDto>> getUsersByKeyword(@RequestParam(name = "keyword") String keyword) {
        Map<String, List<UsersDto>> map = new HashMap<>();
        map.put("result", userService.findByKeyword(keyword));
        return map;
    }

    @GetMapping("user/{id}")
    public ResponseEntity<HttpResponse<Object>> findBYId(@PathVariable Long id) {
//        log.trace("User controller:::getById() {}",id);
        if (userService.getById(id) == null) {
            throw new EntityNotFoundException(Users.class, "id", id.toString());
        } else {
            UsersDto usersDto = userService.getById(id);
            return ResponseEntity.ok()
                    .body(
                            HttpResponse.builder().success(true)
                                    .data(Map.of("user", usersDto))
                                    .status(OK.getReasonPhrase())
                                    .statusCode(OK.value())
                                    .message("User with id " + id + " gotten successfully!!!")
                                    .timestamp(now())
                                    .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse<UserResponse>> findByUserId(@PathVariable Long id, HttpServletRequest request) {
        UserResponse response = userService.findById(id);
        return ResponseEntity.ok()
                .body(
                        HttpResponse.<UserResponse>builder().timestamp(now()).success(true)
                                .message("User gotten successfully")
                                .timestamp(now())
                                .status(OK.getReasonPhrase())
                                .statusCode(OK.value())
                                .data(response)
                                .path(request.getRequestURI())
                                .build());
    }

}
