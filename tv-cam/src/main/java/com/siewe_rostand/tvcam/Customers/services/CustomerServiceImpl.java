package com.siewe_rostand.tvcam.Customers.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siewe_rostand.tvcam.Customers.dto.CustomerMapper;
import com.siewe_rostand.tvcam.Customers.dto.CustomerRequest;
import com.siewe_rostand.tvcam.Customers.dto.CustomerResponse;
import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Customers.repository.CustomersRepository;
import com.siewe_rostand.tvcam.common.constraints.validator.ObjectsValidator;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityAlreadyExistException;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityNotFoundException;
import com.siewe_rostand.tvcam.shared.Exceptions.OperationNotPermittedException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomersRepository customersRepository;
    private final ObjectsValidator<CustomerRequest> validator;
    private final ObjectMapper objectMapper;
    private final CustomerMapper mapper;
    private final CustomerRefNumberGenerator refNumberGenerator;

    @Transactional
    @Override
    public HttpResponse<Object> save(CustomerRequest request) {
        validator.validate(request);
        if (customersRepository.existsByTelephone(request.getTelephone())) {
            throw new EntityAlreadyExistException(
                    "A customer with the telephone number " + request.getTelephone() + " already exist");
        }
        String ref = refNumberGenerator.generateRefNumber();
        if (ref == null || ref.isEmpty()) {
            throw new OperationNotPermittedException("The customer reference has not been generated or it is null");
        }
        Customers customers = mapper.toEntity(request);
        customers.setRef(ref);
        Customers savedCustomers = customersRepository.save(customers);
        Map<String, Object> response = toResponse(savedCustomers);
        return HttpResponse.builder().timestamp(now()).success(true)
                .message("Customer created successfully").data(response)
                .status(CREATED.getReasonPhrase()).statusCode(CREATED.value()).build();
    }

    @Override
    public HttpResponse<Object> update(CustomerRequest customersDto, Long id) {
        Customers existingCustomer = getById(id);
        existingCustomer.setIsActive(customersDto.getIsActive());
        existingCustomer.setName(customersDto.getName());
        existingCustomer.setAddress(customersDto.getAddress());
        existingCustomer.setTelephone(customersDto.getTelephone());
        existingCustomer.setIsSuspended(customersDto.getIsSuspended());
        existingCustomer.setHasDebt(customersDto.getHasDebt());
        Customers updatedCustomers = customersRepository.save(existingCustomer);
        Map<String, Object> response = toResponse(updatedCustomers);
        return HttpResponse.builder().
                timestamp(now()).success(true).
                message("Customer updated successfully").data(response).
                statusCode(OK.value()).status(OK.getReasonPhrase()).build();
    }

    @Override
    public PaginatedResponse findAll(Integer page, Integer size, String sortBy, String direction, String name) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        Page<Customers> customers;
        if (!name.isEmpty()) {
            customers = customersRepository.findAll(pageable);
        } else {
            customers = customersRepository.findAll("%" + name + "%", pageable);
        }
        return buildResponse(customers, pageable);
    }

    private PaginatedResponse buildResponse(Page<Customers> customers, Pageable pageable) {
        Page<CustomerResponse> customersDTOPage = customers.map(mapper::toResponse);
        return PaginatedResponse.builder()
                .timestamp(now())
                .status(OK).statusCode(OK.value())
                .data(customersDTOPage.getContent())
                .message("Customers gotten successfully")
                .lastPage(customersDTOPage.isLast()).firstPage(customersDTOPage.isFirst())
                .totalPages(customersDTOPage.getTotalPages()).totalElements(customersDTOPage.getNumberOfElements())
                .empty(customersDTOPage.isEmpty()).sorted(pageable.getSort().isSorted())
                .numberOfElements(customersDTOPage.getNumberOfElements())
                .page(pageable.getPageNumber()).paged(pageable.isPaged())
                .build();
    }

    @Override
    public Page<CustomerResponse> findAll(Integer page, Integer size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        Page<Customers> customers = customersRepository.findAll(pageable);
        return customers.map(mapper::toResponse);
    }

    @Override
    public HttpResponse<Object> findByKeyword(String keyword) {

        List<Customers> customers = customersRepository.findByKeyword("%" + keyword + "%");
        List<CustomerResponse> responses = customers.stream().map(mapper::toResponse).toList();
        return HttpResponse.builder()
                .timestamp(now()).success(true).
                message("Customers gotten successfully with provided query").data(responses).
                statusCode(OK.value()).status(OK.getReasonPhrase()).build();
    }

    @Override
    public HttpResponse<Object> findById(Long id) {
        Customers customers = getById(id);
        Map<String, Object> response = toResponse(customers);
        return HttpResponse.builder().data(response).status(HttpStatus.OK.getReasonPhrase()).statusCode(HttpStatus.OK.value()).
                message("Customer with id " + id + " gotten successfully!!!").timestamp(now()).success(true)
                .build();
    }

    @Override
    public void delete(Long id) {
        checkIfCustomerExistsOrThrow(id);
        Customers customers = getById(id);

        customers.setIsActive(false);
        customers.setIsSuspended(true);
        customersRepository.save(customers);
    }

    @Override
    public Page<CustomerResponse> findAllActive(Integer page, Integer size, String sortBy, String direction,
                                                Boolean isActive) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        Page<Customers> customers = customersRepository.findAllByIsActive(isActive, pageable);
        return customers.map(mapper::toResponse);
    }

    @Override
    public Customers getById(Long id) {
        return customersRepository.findByCustomerId(id).orElseThrow(() -> new EntityNotFoundException(Customers.class, "id", id.toString()));
    }

    @Override
    public void checkIfCustomerExistsOrThrow(Long id) {
        if (!customersRepository.existsByCustomerId(id)) {
            throw new EntityNotFoundException(Customers.class, "id", id.toString());
        }
    }

    private Map<String, Object> toResponse(Customers customers) {
        CustomerResponse response = mapper.toResponse(customers);
        return objectMapper.convertValue(response, new TypeReference<>() {
        });
    }
}
