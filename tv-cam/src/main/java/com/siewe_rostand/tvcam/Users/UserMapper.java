package com.siewe_rostand.tvcam.Users;

import org.springframework.stereotype.Component;

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
                .telephone(request.getTelephone())
                .password(request.getPassword())
                .build();
    }

    public UserResponse toResponse(Users user) {
        return UserResponse.builder()
                .id(user.getUserId())
                .firstname(user.getFirstname())
                .telephone(user.getTelephone())
                .active(user.getActive())
                .address(user.getAddress())
                .build();
    }
}
