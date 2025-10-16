package com.siewe_rostand.tvcam.Payment.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siewe_rostand.tvcam.Payment.dto.PaymentRequest;
import com.siewe_rostand.tvcam.Payment.dto.PaymentResponse;
import com.siewe_rostand.tvcam.Payment.services.PaymentService;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @PostMapping()
    public ResponseEntity<HttpResponse<Object>> makePayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.save(request);
        Map<String, Object> data = objectMapper
                .convertValue(response, new TypeReference<>() {
                });
        return ResponseEntity.created(URI.create("make_payment")).body(
                HttpResponse.builder()
                        .timestamp(now()).message("payment made successfully")
                        .status(CREATED.getReasonPhrase()).statusCode(CREATED.value()).data(data)
                        .build()
        );
    }

    @GetMapping()
    public ResponseEntity<PaginatedResponse> findAllPayments(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                             @RequestParam(name = "size", defaultValue = "999999") Integer size,
                                                             @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                             @RequestParam(name = "direction", defaultValue = "desc") String direction,
                                                             @RequestParam(name = "name", defaultValue = "") String name) {
        PaginatedResponse response = paymentService.findAll(page, size, sortBy, direction, name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<HttpResponse<Object>> findPaymentByCustomerId(@PathVariable Long customerId) {
        List<PaymentResponse> response = paymentService.findPaymentByCustomerId(customerId);
        return ResponseEntity.ok().body(
                HttpResponse.builder().timestamp(now()).message("Customer's payment gotten successfully").content(response)
                        .statusCode(OK.value()).status(OK.getReasonPhrase()).success(true).build()
        );
    }

    @GetMapping("/all")
    public ResponseEntity<HttpResponse<Object>> findByBillsMonth(@RequestParam("month") Integer month) {
        List<PaymentResponse> response = paymentService.findByBills_Month(month);
        return ResponseEntity.ok().body(
                HttpResponse.builder().timestamp(now()).message("Payment for the month of " + month + " gotten successfully")
                        .statusCode(OK.value()).status(OK.getReasonPhrase()).data(response).success(true).build()
        );
    }
}
