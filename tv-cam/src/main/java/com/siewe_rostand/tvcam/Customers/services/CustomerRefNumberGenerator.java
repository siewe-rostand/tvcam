package com.siewe_rostand.tvcam.Customers.services;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * @author rostand
 * @project tv-cam
 */

@Service
public class CustomerRefNumberGenerator {
  // Use the customer's ID and registration date to generate the reference number
  public String generateRefNumber() {
    String uuid = UUID.randomUUID().toString().substring(0, 8); // Get first 8 characters
        LocalDate currentDate = LocalDate.now();
    return "CLIENT_" + uuid + "_" + currentDate.getYear();
    }

}
