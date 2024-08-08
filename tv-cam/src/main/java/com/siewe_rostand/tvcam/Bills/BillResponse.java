package com.siewe_rostand.tvcam.Bills;

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
public class BillResponse {
    private Long id;
    private BigDecimal amount;
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
}
