package com.siewe_rostand.tvcam.Customers;

import com.siewe_rostand.tvcam.Users.Users;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityAlreadyExistException;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityNotFoundException;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import com.siewe_rostand.tvcam.validator.ObjectsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomersRepository customersRepository;
    private final ObjectsValidator<CustomersDTO> validator;
    private final CustomerRefNumberGenerator refNumberGenerator;


    @Override
    public Customers save(CustomersDTO customersDto) {
        validator.validate(customersDto);
        if (customersRepository.existsByTelephone(customersDto.getTelephone())) {
            throw new EntityAlreadyExistException("A customer with the telephone number " + customersDto.getTelephone() + " already exist");
        } else {
            customersDto.setIsActive(true);
            customersDto.setRef(refNumberGenerator.generateRefNumber(customersDto.getId()));
            customersDto.setIsSuspended(false);
            customersDto.setHasDebt(false);
            Customers customers = new Customers().toMap(customersDto);
            return customersRepository.save(customers);
        }
    }

    @Override
    public Customers update(CustomersDTO customersDto) {
        if (customersRepository.findByCustomerId(customersDto.getId()) == null) {
            throw new EntityNotFoundException(Users.class, "id", customersDto.getId().toString());
        } else {
            Customers existingCustomer = customersRepository.findByCustomerId(customersDto.getId());
            existingCustomer.setIsActive(customersDto.getIsActive());
            existingCustomer.setName(customersDto.getName());
            existingCustomer.setAddress(customersDto.getAddress());
            existingCustomer.setTelephone(customersDto.getTelephone());
            existingCustomer.setIsSuspended(customersDto.getIsSuspended());
            existingCustomer.setHasDebt(customersDto.getHasDebt());
            return customersRepository.saveAndFlush(existingCustomer);
        }
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
        Page<CustomersDTO> customersDTOPage = customers.map(customers1 -> new CustomersDTO().CreateDTO(customers1));
        return PaginatedResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK).statusCode(HttpStatus.OK.value())
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
    public Page<CustomersDTO> findAll(Integer page, Integer size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        Page<Customers> customers = customersRepository.findAll(pageable);
        return customers.map(customers1 -> new CustomersDTO().CreateDTO(customers1));
    }

    @Override
    public List<CustomersDTO> findByKeyword(String keyword) {

        List<Customers> customers = customersRepository.findByKeyword("%" + keyword + "%");
        List<CustomersDTO> customersDTOS = new ArrayList<>();

        for (Customers customer : customers) {
            customersDTOS.add(new CustomersDTO().CreateDTO(customer));
        }
        return customersDTOS;
    }

    @Override
    public CustomersDTO findById(Long id) {
        Customers customers = customersRepository.findByCustomerId(id);
        return new CustomersDTO().CreateDTO(customers);
    }

    @Override
    public void delete(Long id) {
        checkIfCustomerExistsOrThrow(id);
        Customers customers = customersRepository.findByCustomerId(id);

        if (Optional.ofNullable(customers).isPresent())
            customersRepository.deleteById(id);
    }

    @Override
    public Page<CustomersDTO> findAllActive(Integer page, Integer size, String sortBy, String direction, Boolean isActive) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        Page<Customers> customers = customersRepository.findAllByIsActive(isActive, pageable);
        return customers.map(customers1 -> new CustomersDTO().CreateDTO(customers1));
    }

    @Override
    public void checkIfCustomerExistsOrThrow(Long id) {
        if (!customersRepository.existsByCustomerId(id)) {
            throw new EntityNotFoundException(Customers.class, "id", id.toString());
        }
    }
}
