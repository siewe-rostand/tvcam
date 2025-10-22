package com.siewe_rostand.tvcam.Bills.dto;


import com.siewe_rostand.tvcam.Bills.model.Bills;
import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.siewe_rostand.tvcam.shared.utils.CommonUtils.DEFAULT_MONTHLY_AMOUNT;
import static com.siewe_rostand.tvcam.shared.utils.CommonUtils.FORMATTER;

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
                .remainingBalance(bills.getNetToPay().subtract(bills.getPaidAmount()))
                .build();
    }

    public BillRequest toBillRequest(Bills bills) {
        return BillRequest.builder()
                .month(bills.getMonth())
                .year(bills.getYear())
                .paidAmount(bills.getPaidAmount())
                .monthlyPayment(bills.getMonthlyPayment())
                .debt(bills.getDebt())
                .deadline(bills.getDeadline())
                .depositDate(bills.getDepositDate())
                .penalties(bills.getPenalties())
                .observation(bills.getObservation())
                .build();
    }

    public void updateExistingBill(Bills bill, BillRequest request, BigDecimal netToPay, BigDecimal debt, LocalDateTime billingDate) {
        bill.setMonthlyPayment(getMonthlyPaymentOrDefault(request.getMonthlyPayment()));
        bill.setDeadline(getDeadlineOrDefault(request.getDeadline(), billingDate));
        bill.setPaymentStatus(PaymentStatus.UNPAID);
        bill.setDebt(debt);
        bill.setObservation(request.getObservation());
        bill.setDepositDate(getDepositDateOrDefault(request.getDepositDate(), billingDate));
        bill.setPenalties(request.getPenalties());
        bill.setCurrentPeriodBill(true);
        bill.setPaidAmount(getPaidAmountOrDefault(request.getPaidAmount()));
        bill.setNetToPay(netToPay);
    }

    public Bills createNewBill(Customers customer, BillRequest request, BigDecimal netToPay, BigDecimal debt,
                                LocalDateTime billingDate, int month, int year) {
        return Bills.builder()
                .customers(customer)
                .monthlyPayment(getMonthlyPaymentOrDefault(request.getMonthlyPayment()))
                .deadline(getDeadlineOrDefault(request.getDeadline(), billingDate))
                .paymentStatus(PaymentStatus.UNPAID)
                .debt(debt)
                .observation(request.getObservation())
                .month(month)
                .year(year)
                .depositDate(getDepositDateOrDefault(request.getDepositDate(), billingDate))
                .penalties(request.getPenalties())
                .currentPeriodBill(true)
                .paidAmount(getPaidAmountOrDefault(request.getPaidAmount()))
                .netToPay(netToPay)
                .build();
    }

    // Helper methods to reduce duplication
    private BigDecimal getMonthlyPaymentOrDefault(BigDecimal monthlyPayment) {
        return monthlyPayment != null ? monthlyPayment : DEFAULT_MONTHLY_AMOUNT;
    }

    private String getDeadlineOrDefault(String deadline, LocalDateTime billingDate) {
        if (deadline != null && !deadline.isEmpty()) {
            return deadline;
        }
        return FORMATTER.format(billingDate.plusDays(10));
    }

    private String getDepositDateOrDefault(String depositDate, LocalDateTime billingDate) {
        return depositDate != null ? depositDate : FORMATTER.format(billingDate);
    }

    private BigDecimal getPaidAmountOrDefault(BigDecimal paidAmount) {
        return paidAmount != null ? paidAmount : BigDecimal.ZERO;
    }
}
