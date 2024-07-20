package com.siewe_rostand.tvcam.Customers;

import com.siewe_rostand.tvcam.Users.Users;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityNotFoundException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("api/v1/customers")
@RestController
public class CustomerController {
    private final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerServiceImpl customerService;

    public CustomerController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/save")
    public ResponseEntity<HttpResponse> createCustomer(@RequestBody CustomersDTO customersDto) {
        log.error("Customer controller -> createCustomer(): {}",customersDto);
        CustomersDTO customersDTO1 = new CustomersDTO().CreateDTO(customerService.save(customersDto));
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder().timestamp(LocalDateTime.now())
                        .message("Customer created successfully").data(Map.of("Customer", customersDTO1))
                        .status(HttpStatus.CREATED).statusCode(HttpStatus.CREATED.value()).build()
        );
    }

    @PutMapping("/customers")
    public ResponseEntity<HttpResponse> updateCustomer(@RequestBody CustomersDTO customersDto) {
        log.error("Customer controller -> updateCustomer(): {}",customersDto);
//        log.trace("Customer controller:::updateCustomer() {}",customersDto);
        CustomersDTO customersDTO1 = new CustomersDTO().CreateDTO(customerService.update(customersDto));

        return ResponseEntity.ok().body(HttpResponse.builder().
                timestamp(LocalDateTime.now()).
                message("Customer updated successfully").data(Map.of("Customer", customersDTO1)).
                statusCode(HttpStatus.OK.value()).status(HttpStatus.OK).build());
    }

    @GetMapping("/customers")
    public ResponseEntity<PaginatedResponse> getAllCustomers(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                              @RequestParam(name = "size", defaultValue = "999999") Integer size,
                                              @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                              @RequestParam(name = "direction", defaultValue = "desc") String direction,
                                              @RequestParam(name = "name", defaultValue = "") String name) {
        PaginatedResponse customersDTOPage = customerService.findAll(page, size, sortBy, direction,name);
        return  ResponseEntity.ok().body(customersDTOPage);
    }

    @GetMapping("/customers/search")
    public Map<String, List<CustomersDTO>> getCustomerByKeyword(@RequestParam(name = "keyword") String keyword) {
        Map<String, List<CustomersDTO>> map = new HashMap<>();
        map.put("result", customerService.findByKeyword(keyword));
        return map;
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<HttpResponse> getById(@PathVariable Long id) {
        log.trace("Customer controller:::getById() {}",id);
        if (customerService.findById(id) == null) {
            throw new EntityNotFoundException(Users.class,"id",id.toString());
        } else {
            CustomersDTO customersDto = customerService.findById(id);
            return ResponseEntity.ok().body(
                    HttpResponse.builder().data(Map.of("customer", customersDto)).status(HttpStatus.OK).statusCode(HttpStatus.OK.value()).
                            message("Customer with id " + id + " gotten successfully!!!").timestamp(LocalDateTime.now())
                            .build());
        }
    }
}
