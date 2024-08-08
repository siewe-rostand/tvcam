package com.siewe_rostand.tvcam.Customers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomersRepository extends JpaRepository<Customers, Long> {

    Customers findByCustomerId(Long id);

    Boolean existsByTelephone(String telephone);

    Boolean existsByCustomerId(Long customerId);


    @Query("SELECT cus FROM Customers cus WHERE (cus.name LIKE ?1 OR ?1 IS NULL)")
    Page<Customers> findAll(String name,Pageable pageable);

    Page<Customers> findAll(Pageable pageable);

    @Query("SELECT cus FROM Customers cus WHERE cus.name LIKE ?1")
    List<Customers> findByKeyword(String keyword);

    @Query("SELECT cus FROM Customers cus WHERE cus.name LIKE ?1")
    Page<Customers> findByKeyword(String keyword, Pageable pageable);

    Page<Customers> findAllByIsActive(boolean isActive,Pageable pageable);
}
