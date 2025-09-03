package com.siewe_rostand.tvcam.Users.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final Logger logger = LoggerFactory.getLogger(UsersController.class);

    private final UserService userService;

    @PostMapping
    public ResponseEntity<HttpResponse<Object>> saveUser(@RequestBody UserRequest usersDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserResponse dto = userService.create(usersDto);
        Map<String, Object> data = objectMapper
                .convertValue(dto, new TypeReference<>() {
                });
        return ResponseEntity.created(URI.create(""))
                .body(
                        HttpResponse.builder()
                                .timestamp(now())
                                .message("User created successfully")
                                .data(data)
                                .status(HttpStatus.CREATED)
                                .statusCode(HttpStatus.CREATED.value())
                                .build());
    }

    @PutMapping("/edit")
    public ResponseEntity<HttpResponse<Object>> updateUser(@RequestBody UsersDto usersDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        UsersDto dto = new UsersDto().CreateDTO(userService.updateUser(usersDto));
        Map<String, Object> data = objectMapper.convertValue(dto, new TypeReference<>() {
        });
        return ResponseEntity.ok()
                .body(
                        HttpResponse.builder()
                                .timestamp(now())
                                .message("User data updated successfully")
                                .data(data)
                                .status(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse> getAllUsers(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(name = "size", defaultValue = "999999") Integer size,
                                                         @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                         @RequestParam(name = "direction", defaultValue = "desc") String direction,
                                                         @RequestParam(name = "name", defaultValue = "") String name) {
        PaginatedResponse response = userService.findAll(page, size, sortBy, direction, name);
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
                            HttpResponse.builder()
                                    .data(Map.of("user", usersDto))
                                    .status(HttpStatus.OK)
                                    .statusCode(HttpStatus.OK.value())
                                    .message("User with id " + id + " gotten successfully!!!")
                                    .timestamp(now())
                                    .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse<Object>> findByUserId(@PathVariable Long id, HttpServletRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserResponse response = userService.findById(id);
        Map<String, Object> data = objectMapper.convertValue(response, new TypeReference<>() {
        });

        logger.debug("findByUserId data : {}", data);
        return ResponseEntity.ok()
                .body(
                        HttpResponse.builder()
                                .message("User gotten successfully")
                                .timestamp(now())
                                .status(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .data(Map.of("user", response))
                                .path(request.getRequestURI())
                                .build());
    }

}
