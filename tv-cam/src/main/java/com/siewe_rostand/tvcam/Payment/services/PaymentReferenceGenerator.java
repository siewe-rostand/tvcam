package com.siewe_rostand.tvcam.Payment.services;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author rostand
 * @project tv-cam
 */

@Component
public class PaymentReferenceGenerator {

    private static final AtomicLong SEQUENCE = new AtomicLong(0);

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int RANDOM_PART_LENGTH = 4;
    private static final int MAX_SEQUENCE_VALUE = 999;

    public String generatePaymentReference() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String dateTimePart = currentDateTime.format(FORMATTER);
        long currentSequence = SEQUENCE.getAndIncrement();
        long wrappedSequence = currentSequence % (MAX_SEQUENCE_VALUE + 1);
        String seqPart = String.format("%03d", wrappedSequence);
        String randomPart = generateRandomAlphanumeric();
        return "PAIE" + "-" + dateTimePart + "-" + seqPart + "-" + randomPart;
    }

    private String generateRandomAlphanumeric() {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";
        String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;

        StringBuilder sb = new StringBuilder(PaymentReferenceGenerator.RANDOM_PART_LENGTH);
        for (int i = 0; i < PaymentReferenceGenerator.RANDOM_PART_LENGTH; i++) {
            int randomCharIndex = SECURE_RANDOM.nextInt(DATA_FOR_RANDOM_STRING.length());
            sb.append(DATA_FOR_RANDOM_STRING.charAt(randomCharIndex));
        }
        return sb.toString();
    }

}
