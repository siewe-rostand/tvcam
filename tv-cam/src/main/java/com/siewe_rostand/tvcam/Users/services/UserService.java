package com.siewe_rostand.tvcam.Users.services;

import com.siewe_rostand.tvcam.Users.dto.UserRequest;
import com.siewe_rostand.tvcam.Users.dto.UserResponse;
import com.siewe_rostand.tvcam.Users.dto.UsersDto;
import com.siewe_rostand.tvcam.Users.models.Users;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;

import java.util.List;

public interface UserService {
    Users saveUser(UsersDto usersDto);

    Users updateUser(UsersDto usersDto);

    PaginatedResponse<UserResponse> findAll(Integer page, Integer size, String sortBy, String direction, String name);

    List<UsersDto> findByKeyword(String keyword);

    UserResponse findById(Long id);

    UserResponse create(UserRequest request);

    UserResponse invalidateAccount(Long userId);

    List<UserResponse> findAllUsersByState(boolean active);

    UsersDto getById(Long id);

    void delete(Long id);

    Users findUserById(Long id);
}
