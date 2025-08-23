package com.siewe_rostand.tvcam.Bills.dto;

import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author rostand
 * @project tv-cam
 * 
 *          Summary DTO for Bills to optimize database queries and reduce data
 *          transfer
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillSummary {

    // Essential Bill Information
    private Long billId;
    private String month;
    private String year;
    private BigDecimal netToPay;
    private BigDecimal monthlyPayment;
    private BigDecimal debt;
    private PaymentStatus paymentStatus;
    private boolean currentPeriodBill;
    private String deadline;

    // Customer Information (to avoid joins)
    private Long customerId;
    private String customerName;
    private String customerRef;

    // Audit Information
    private String createdAt;
    private String updatedAt;

    // Constructor for common queries (minimal fields)
    public BillSummary(Long billId, String month, BigDecimal netToPay, String customerName) {
        this.billId = billId;
        this.month = month;
        this.netToPay = netToPay;
        this.customerName = customerName;
    }

    // Constructor for detailed summary
    public BillSummary(Long billId, String month, String year, BigDecimal netToPay,
            PaymentStatus paymentStatus, Long customerId, String customerName) {
        this.billId = billId;
        this.month = month;
        this.year = year;
        this.netToPay = netToPay;
        this.paymentStatus = paymentStatus;
        this.customerId = customerId;
        this.customerName = customerName;
    }
}
