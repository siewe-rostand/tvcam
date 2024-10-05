package com.siewe_rostand.tvcam.Bills;

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
    HttpResponse findCustomerBills(Long customerId);
    HttpResponse delete(Long id);
    BillResponse generateBills(BillRequest request);
    List<BillResponse> generateBillsForSelectedCustomers(List<Long> customerIds,Boolean shouldGenerate);
}
