package com.siewe_rostand.tvcam.Bills.services;

import com.siewe_rostand.tvcam.Bills.dto.BillRequest;
import com.siewe_rostand.tvcam.Bills.dto.BillResponse;
import com.siewe_rostand.tvcam.Bills.dto.BillSDto;
import com.siewe_rostand.tvcam.Bills.model.Bills;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;

import java.util.List;

/**
 * @author rostand
 * @project tv-cam
 */
public interface BillServices {
    BillResponse save(BillRequest request);

    Bills update(BillSDto billSDto);

    PaginatedResponse findAll(Integer page, Integer size, String sortBy, String direction, String name);

    HttpResponse<Object> findCustomerBills(Long customerId);

    HttpResponse<Object> delete(Long id);

    BillResponse generateBills(BillRequest request);

    List<BillResponse> generateBillsForSelectedCustomers(List<Long> customerIds, Boolean shouldGenerate);
    
    /**
     * Récupère les factures pour impression
     */
    List<BillResponse> getBillsForPrint(List<Long> billIds);
    
    /**
     * Vérifie l'existence de factures pour un mois donné
     */
    boolean checkExistingBillsForMonth(List<Long> customerIds, String month, String year);
    
    /**
     * Supprime plusieurs factures en lot
     */
    int deleteBillsBatch(List<Long> billIds);
}
