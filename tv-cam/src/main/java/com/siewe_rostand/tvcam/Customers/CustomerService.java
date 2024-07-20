package com.siewe_rostand.tvcam.Customers;

import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {

    Customers save(CustomersDTO customersDto);
    Customers update(CustomersDTO customersDto);
    Page<CustomersDTO> findAll(Integer page, Integer size, String sortBy, String direction);
    PaginatedResponse findAll(Integer page, Integer size, String sortBy, String direction, String name);
    List<CustomersDTO> findByKeyword(String keyword);
    CustomersDTO findById(Long id);
    void delete(Long id);
    Page<CustomersDTO> findAllActive(Integer page, Integer size, String sortBy, String direction, Boolean isActive);
}
