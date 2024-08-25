package com.siewe_rostand.tvcam.Bills;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siewe_rostand.tvcam.exceptions.ApiException;
import com.siewe_rostand.tvcam.exceptions.ResourceNotFoundException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


/**
 * @author rostand
 * @project tv-cam
 */

@RestController
@RequestMapping("bills")
public class BillController {
    private final Logger logger = LoggerFactory.getLogger(BillController.class);

    private final BillServices billServices;

    public BillController(BillServices billServices) {
        this.billServices = billServices;
    }

    @PostMapping("/save")
    public ResponseEntity<HttpResponse> saveBill(@RequestBody BillRequest request) {
        logger.debug("BillController:::saveBills {}", request);

        ObjectMapper objectMapper = new ObjectMapper();
        BillResponse response = billServices.generateBills(request);
        Map<String, Object> data = objectMapper
                .convertValue(response, new TypeReference<>() {
                });
        return ResponseEntity.created(URI.create("save_bill")).body(
                HttpResponse.builder()
                        .timestamp(now()).message("Bills created successfully")
                        .status(CREATED).statusCode(CREATED.value()).data(data)
                        .build()
        );
    }

    @PostMapping("/generate")
    public ResponseEntity<HttpResponse> generateBillsForSelectedCustomers(@RequestBody List<Long> customerIds) {
        logger.debug("BillController:::generateBillsForSelectedCustomers {}", customerIds);
        try {
            var response = billServices.generateBillsForSelectedCustomers(customerIds);
            return ResponseEntity.status(CREATED).body(
                    HttpResponse.builder().timestamp(now()).message("selected users bills created successfully")
                            .content(response)
                            .status(CREATED).statusCode(CREATED.value()).build()
            );
        } catch (Exception e) {
            throw new ApiException(e.getMessage(), "An error occurred while generating bills ");
        }
    }

    @GetMapping()
    public ResponseEntity<PaginatedResponse> getAllBills(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(name = "size", defaultValue = "999999") Integer size,
                                                         @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                         @RequestParam(name = "direction", defaultValue = "desc") String direction,
                                                         @RequestParam(name = "name", defaultValue = "") String name) {
        PaginatedResponse response = billServices.findAll(page, size, sortBy, direction, name);
        return ResponseEntity.status(OK).body(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<HttpResponse> getBillsByCustomerId(@PathVariable(name = "customerId") Long customerId) {
        logger.debug("BillController::getBillsByCustomerId {}", customerId);
        HttpResponse response = billServices.findCustomerBills(customerId);

        return ResponseEntity.status(OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> deleteBill(@PathVariable(name = "id") Long id) {
        logger.debug("BillController:::deleteBill {}", id);
        HttpResponse response = billServices.delete(id);
        return ResponseEntity.ok().body(response);
    }

}
