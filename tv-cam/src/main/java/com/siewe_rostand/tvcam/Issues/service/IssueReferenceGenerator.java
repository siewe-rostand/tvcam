package com.siewe_rostand.tvcam.Issues.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service for generating unique issue references
 * 
 * @author rostand
 * @project tv-cam
 */
@Service
public class IssueReferenceGenerator {

    private static final String PREFIX = "ISS";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String generateReference() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DATE_FORMAT);
        String timePart = String.format("%02d%02d", now.getHour(), now.getMinute());
        String randomPart = String.format("%03d", ThreadLocalRandom.current().nextInt(1, 1000));
        
        return PREFIX + "-" + datePart + "-" + timePart + "-" + randomPart;
    }
}