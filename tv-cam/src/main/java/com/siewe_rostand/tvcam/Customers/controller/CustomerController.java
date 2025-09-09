package com.siewe_rostand.tvcam.Customers.controller;

import com.siewe_rostand.tvcam.Customers.dto.CustomerRequest;
import com.siewe_rostand.tvcam.Customers.services.CustomerService;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static java.time.LocalDateTime.now;

@RequestMapping("customers")
@RestController
public class CustomerController {
    private final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<HttpResponse<Object>> createCustomer(@RequestBody CustomerRequest customersDto) {
        log.info("Customer controller -> createCustomer(): {}", customersDto);
        return ResponseEntity.created(URI.create("")).body(
                customerService.save(customersDto)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse<Object>> updateCustomer(@RequestBody CustomerRequest request, @PathVariable Long id) {
        log.error("Customer controller -> updateCustomer(): {} with id {}", request, id);
        return ResponseEntity.ok().body(customerService.update(request, id));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse> getAllCustomers(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                             @RequestParam(name = "size", defaultValue = "999999") Integer size,
                                                             @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                             @RequestParam(name = "direction", defaultValue = "desc") String direction,
                                                             @RequestParam(name = "name", defaultValue = "") String name) {
        PaginatedResponse customersDTOPage = customerService.findAll(page, size, sortBy, direction, name);
        return ResponseEntity.ok().body(customersDTOPage);
    }

    @GetMapping("/search")
    public ResponseEntity<HttpResponse<Object>> getCustomerByKeyword(@RequestParam(name = "keyword") String keyword) {

        return ResponseEntity.ok().body(customerService.findByKeyword(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse<Object>> getById(@PathVariable Long id) {
        log.trace("Customer controller:::getById() {}", id);
        return ResponseEntity.ok().body(
                customerService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse<Object>> deleteById(@PathVariable Long id) {
        log.debug("Customer controller:::deleteById() {}", id);
        customerService.delete(id);
        return ResponseEntity.ok().body(
                HttpResponse.builder().success(true).status(HttpStatus.OK.getReasonPhrase()).statusCode(HttpStatus.OK.value()).
                        message("Customer with id " + id + " Deleted successfully!!!").timestamp(now())
                        .build());
    }
}
