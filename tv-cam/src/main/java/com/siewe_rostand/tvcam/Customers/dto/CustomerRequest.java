package com.siewe_rostand.tvcam.Customers.dto;

import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import static com.siewe_rostand.tvcam.shared.utils.CommonUtils.REGEX_DIGIT_ONLY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {
    @NotNull(message = "customer name can not be null or empty")
    private String name;

    private String address;

    @Min(9)
    @Pattern(regexp = REGEX_DIGIT_ONLY, message = "Telephone must only include number(digits)")
    private String telephone;


    private Boolean hasDebt = false;

    private Boolean hasPaid = false;

    private Boolean isActive = true;

    private Boolean isSuspended = false;

    private String lastBillGenerationDate;

    private String paymentFrequency = PaymentFrequency.DEFAULT.name();
}
