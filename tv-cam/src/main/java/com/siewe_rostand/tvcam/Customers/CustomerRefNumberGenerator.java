package com.siewe_rostand.tvcam.Customers;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * @author rostand
 * @project tv-cam
 */

@Service
public class CustomerRefNumberGenerator {
    // Use the customer's ID and registration date to generate the reference number
    public String generateRefNumber(Long customerId) {
        LocalDate currentDate = LocalDate.now();
        return "CUST-" + customerId + "-" + currentDate.getYear();
    }

}
