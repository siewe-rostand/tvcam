package com.siewe_rostand.tvcam.Users;

import com.siewe_rostand.tvcam.Roles.Roles;
import com.siewe_rostand.tvcam.Roles.RolesRepository;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityAlreadyExistException;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityNotFoundException;
import com.siewe_rostand.tvcam.shared.ObjectsValidator;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final ObjectsValidator<UserRequest> validator;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    public Users toMap(UsersDto usersDto) {
        HashSet<Roles> roles = new HashSet<>();
        if (usersDto.getRoles() != null) {
            for (String role : usersDto.getRoles()) {
                roles.add(rolesRepository.findByName(role));
            }
        }
        if (usersDto.getActivated() == null) {
            usersDto.setActivated(true);
        }
        return Users.builder().userId(usersDto.getId()).firstname(usersDto.getFirstname()).address(usersDto.getLastname())
                .telephone(usersDto.getTelephone()).address(usersDto.getAddress()).active(usersDto.getActivated()).roles(roles)
                .build();
    }

    @Override
    public Users saveUser(UsersDto usersDto) {
        log.error("user dto {}", usersDto);
        if (findByTelephone(usersDto.getTelephone()) != null) {
            throw new EntityAlreadyExistException("A user with the telephone number " + usersDto.getTelephone() + " already exist");
        } else {
            Users users = toMap(usersDto);
            return usersRepository.save(users);
        }
    }

    @Override
    public Users updateUser(UsersDto usersDto) {
        if (usersRepository.findByUserId(usersDto.getId()) == null) {
            throw new EntityNotFoundException(Users.class, "id", usersDto.getId().toString());
        } else {
            Users users = toMap(usersDto);
            return usersRepository.save(users);
        }
    }

    @Override
    public PaginatedResponse findAll(Integer page, Integer size, String sortBy, String direction, String name) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        Page<Users> users = usersRepository.findAll(name, pageable);
        Page<UserResponse> responses = users.map(mapper::toResponse);
        return PaginatedResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK).statusCode(HttpStatus.OK.value())
                .message("Users gotten successfully").data(responses.getContent())
                .lastPage(responses.isLast()).firstPage(responses.isFirst())
                .totalPages(responses.getTotalPages()).totalElements(responses.getNumberOfElements())
                .empty(responses.isEmpty()).sorted(responses.getSort().isSorted())
                .numberOfElements(responses.getNumberOfElements())
                .paged(pageable.isPaged()).page(pageable.getPageNumber())
                .build();
    }

    @Override
    public List<UsersDto> findByKeyword(String keyword) {
        List<Users> usersList = usersRepository.findAllByKeyword("%" + keyword + "%");

        List<UsersDto> usersDtos = new ArrayList<>();
        for (Users users : usersList) {
            usersDtos.add(new UsersDto().CreateDTO(users));
        }
        return usersDtos;
    }

    @Override
    public UserResponse findById(Long id) {
        return usersRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("No user found with the ID:: " + id));
    }

    @Override
    public UserResponse create(UserRequest request) {
        validator.validate(request);
        Users user = mapper.toUser(request);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setActive(true);
        Users saveUser = usersRepository.save(user);
        return mapper.toResponse(saveUser);
    }

    @Override
    public UserResponse invalidateAccount(Long userId) {
        var user = usersRepository.findById(userId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("No user found with the ID for account invalidation:: " + userId));
        user.setActive(false);
        return mapper.toResponse(user);
    }

    @Override
    public List<UserResponse> findAllUsersByState(boolean active) {
        return usersRepository.findAllByActive(active)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsersDto getById(Long id) {
        if (usersRepository.findByUserId(id) == null) {
            throw new EntityNotFoundException(Users.class, "id", id.toString());
        }
        Users users = usersRepository.findByUserId(id);
        return new UsersDto().CreateDTO(users);
    }

    @Override
    public void delete(Long id) {
        if (usersRepository.findByUserId(id) == null) {
            throw new EntityNotFoundException(Users.class, "id", id.toString());
        }
        usersRepository.deleteById(id);
    }

    public Users findByTelephone(String telephone) {
        return usersRepository.findByTelephone(telephone);
    }
}
