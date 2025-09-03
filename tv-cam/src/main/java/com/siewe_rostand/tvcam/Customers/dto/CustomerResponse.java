package com.siewe_rostand.tvcam.Customers.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    private Long id;
    private String name;

    private String address;
    private String ref;
    private String telephone;

    private Boolean hasDebt;

    private Boolean hasPaid;

    private Boolean isActive;

    private Boolean isSuspended;

    private String lastBillGenerationDate;

    private String paymentFrequency;
}
