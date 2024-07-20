package com.siewe_rostand.tvcam.Users;

import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    Users saveUser(UsersDto usersDto);
    Users updateUser(UsersDto usersDto);
    PaginatedResponse findAll(Integer page, Integer size, String sortBy, String direction, String name);
    List<UsersDto> findByKeyword(String keyword);
    UserResponse findById(Long id);
    UserResponse create(UserRequest request);
    UserResponse invalidateAccount(Long userId);
    List<UserResponse> findAllUsersByState(boolean active);
    UsersDto getById(Long id);
    void delete(Long id);
}
