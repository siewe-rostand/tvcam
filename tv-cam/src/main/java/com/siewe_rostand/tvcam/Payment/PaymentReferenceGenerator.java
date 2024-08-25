package com.siewe_rostand.tvcam.Payment;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author rostand
 * @project tv-cam
 */

@Component
public class PaymentReferenceGenerator {

    private final AtomicInteger sequence = new AtomicInteger(1);

    public String generatePaymentReference() {
        LocalDate currentDate = LocalDate.now();
        String datePart = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMddHHss"));
        String seqPart = String.format("%04d", sequence.getAndIncrement());
        return "PAIE"+ datePart + "-" + seqPart;
    }

}
