package com.siewe_rostand.tvcam.Bills;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

/**
 * @author rostand
 * @project tv-cam
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BillResponse {
    private Long id;
    private BigDecimal paidAmount;
    private BigDecimal monthlyPayment;
    private String year;
    private String month;
    private String depositDate;
    private String deadLine;
    private BigDecimal debt;
    private Integer penalties;
    private BigDecimal netToPay;
    private String observation;
    private String customerName;
    private Long customerId;
    private String status;
    private BigDecimal remainingBalance;
}
