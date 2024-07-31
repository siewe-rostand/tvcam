package com.siewe_rostand.tvcam.Users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Users findByUserId(Long id);

    Users findByTelephone(String telephone);

    Optional<Users> getByTelephone(String telephone);

    Boolean existsByTelephone(String telephone);

    @Query("select u from Users u JOIN u.roles r WHERE r.name = ?1")
    List<Users> findAllByRole(String role);

    @Query("SELECT u FROM Users  u JOIN u.roles r WHERE r.name = ?1 AND u.active IS NOT NULL OR u.active  = true")
    Page<Users> findByRole(String role, Pageable pageable);

    @Query("SELECT u FROM Users u WHERE u.firstname = ?1 OR u.lastname = ?1 OR u.telephone = ?1 ORDER BY u.createdAt")
    List<Users> findAllByKeyword(String keyword);

    @Query("select u from Users u "
            + "where u.lastname like ?1 or u.firstname like ?1 or ?1 is null")
    Page<Users> findAll(String name, Pageable pageable);

    Page<Users> findAll(Pageable pageable);

    List<Users> findAllByActive(boolean active);
}
