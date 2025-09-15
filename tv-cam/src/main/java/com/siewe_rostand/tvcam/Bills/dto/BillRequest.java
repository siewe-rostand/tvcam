package com.siewe_rostand.tvcam.Bills.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

import static com.siewe_rostand.tvcam.shared.utils.CommonUtils.DATE_FORMAT_REGEX;

/**
 * @author rostand
 * @project tv-cam
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BillRequest {

    @NotNull(message = "Month cannot be null.")
    @Min(value = 1, message = "Month must be between 1 and 12.")
    @Max(value = 12, message = "Month must be between 1 and 12.")
    private Integer month;

    @NotNull(message = "Year cannot be null.")
    @Min(value = 2020, message = "Year must be a 4-digit number.")
    @Max(value = 2100, message = "Year must be a 4-digit number.")
    private Integer year;

    @NotBlank(message = "Deposit date cannot be empty")
    @Pattern(regexp = DATE_FORMAT_REGEX, message = "Deposit date must be in YYYY-MM-DD format.")
    private String depositDate;

    @NotBlank(message = "Deadline cannot be empty")
    @Pattern(regexp = DATE_FORMAT_REGEX, message = "Deadline must be in YYYY-MM-DD format.")
    private String deadline;

    @Size(max = 255, message = "Observation must be less than 255 characters.")
    private String observation;

    @PositiveOrZero(message = "Paid amount must be a positive number or zero.")
    private BigDecimal paidAmount;

    @PositiveOrZero(message = "Monthly payment must be a positive number or zero.")
    private BigDecimal monthlyPayment;

    @PositiveOrZero(message = "Penalties must be a positive number or zero.")
    private Integer penalties;

    @PositiveOrZero(message = "Debt must be a positive number or zero.")
    private BigDecimal debt;

    @NotNull(message = "Customer ID cannot be null.")
    @Positive(message = "Customer ID must be a positive number.")
    private Long customerId;
}

