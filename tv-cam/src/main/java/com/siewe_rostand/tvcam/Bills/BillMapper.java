package com.siewe_rostand.tvcam.Bills;


import org.springframework.stereotype.Component;

/**
 * @author rostand
 * @project tv-cam
 */

@Component
public class BillMapper {

    public Bills toBills(BillRequest request) {
        return Bills.builder()
                .month(request.getMonth())
                .year(request.getYear())
                .paidAmount(request.getPaidAmount())
                .monthlyPayment(request.getMonthlyPayment())
                .debt(request.getDebt())
                .deadline(request.getDeadline())
                .depositDate(request.getDepositDate())
                .penalties(request.getPenalties())
                .observation(request.getObservation())
                .build();
    }

    public BillResponse toResponse(Bills bills) {
        return BillResponse.builder()
                .id(bills.getBillId())
                .month(bills.getMonth())
                .paidAmount(bills.getPaidAmount())
                .monthlyPayment(bills.getMonthlyPayment())
                .year(bills.getYear())
                .debt(bills.getDebt())
                .depositDate(bills.getDepositDate())
                .deadLine(bills.getDeadline())
                .penalties(bills.getPenalties())
                .netToPay(bills.getNetToPay())
                .observation(bills.getObservation())
                .customerName(bills.getCustomers().getName())
                .customerId(bills.getCustomers().getCustomerId())
                .status(bills.getPaymentStatus().name())
                .build();
    }
}
