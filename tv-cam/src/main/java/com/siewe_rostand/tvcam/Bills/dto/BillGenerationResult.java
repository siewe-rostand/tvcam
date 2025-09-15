package com.siewe_rostand.tvcam.Bills.dto;

import lombok.*;

import java.util.List;

/**
 * Result object for bill generation operations
 * 
 * @author rostand
 * @project tv-cam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillGenerationResult {
    private List<BillResponse> generatedBills;
    private List<ExistingBillInfo> existingBills;
    private boolean hasExistingBills;
    private String message;
    private boolean success;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExistingBillInfo {
        private Long customerId;
        private String customerName;
        private Long billId;
        private Integer month;
        private Integer year;
    }
}
