package com.siewe_rostand.tvcam.Customers.services;

import com.siewe_rostand.tvcam.Customers.dto.CustomerRequest;
import com.siewe_rostand.tvcam.Customers.dto.CustomerResponse;
import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import org.springframework.data.domain.Page;

public interface CustomerService {

    HttpResponse<Object> save(CustomerRequest customersDto);

    HttpResponse<Object> update(CustomerRequest request, Long id);

    Page<CustomerResponse> findAll(Integer page, Integer size, String sortBy, String direction);

    PaginatedResponse findAll(Integer page, Integer size, String sortBy, String direction, String name);

    HttpResponse<Object> findByKeyword(String keyword);

    HttpResponse<Object> findById(Long id);

    void delete(Long id);

    void checkIfCustomerExistsOrThrow(Long id);

    Page<CustomerResponse> findAllActive(Integer page, Integer size, String sortBy, String direction, Boolean isActive);

    Customers getById(Long id);
}
