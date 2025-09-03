package com.siewe_rostand.tvcam.Customers.services;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author rostand
 * @project tv-cam
 */

@Service
public class CustomerRefNumberGenerator {
    public String generateRefNumber() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        LocalDate currentDate = LocalDate.now();
        return "CLIENT_" + uuid + "_" + currentDate.getYear();
    }

}
