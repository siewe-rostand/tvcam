package com.siewe_rostand.tvcam.Bills;

import jakarta.validation.constraints.NotNull;
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
public class BillRequest {

    private String month;

    private String year;

    private String depositDate;

    private String deadline;

    private String observation;

    private BigDecimal amount;

    private Integer penalties;

    private BigDecimal debt;

    private Long customerId;

}
