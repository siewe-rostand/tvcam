package com.siewe_rostand.tvcam.Users.dto;

import com.siewe_rostand.tvcam.Roles.Roles;
import com.siewe_rostand.tvcam.Users.models.Users;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * @author rostand
 * @project tv-cam
 */

@Component
public class UserMapper {

    public Users toUser(UserRequest request) {
        return Users.builder()
                .userId(request.getId())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .telephone(request.getTelephone())
                .telephoneAlt(request.getTelephoneAlt())
                .password(request.getPassword())
                .build();
    }

    public UserResponse toResponse(Users user) {
        return UserResponse.builder()
                .id(user.getUserId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .fullname(user.getFullName())
                .telephone(user.getTelephone())
                .telephoneAlt(user.getTelephoneAlt())
                .role(user.getRoles().stream().map(Roles::getName).collect(Collectors.toSet()))
                .active(user.getActive())
                .address(user.getAddress())
                .created_at(user.getCreatedAt())
                .build();
    }
}
