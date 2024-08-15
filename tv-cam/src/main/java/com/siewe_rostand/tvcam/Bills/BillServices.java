package com.siewe_rostand.tvcam.Bills;

import com.siewe_rostand.tvcam.exceptions.ResourceNotFoundException;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;

import java.util.List;

/**
 * @author rostand
 * @project tv-cam
 */
public interface BillServices {
    BillResponse save(BillRequest request);
    Bills update(BillSDto billSDto);
    PaginatedResponse findAll(Integer page, Integer size, String sortBy, String direction, String name) throws ResourceNotFoundException;
    BillSDto findById(Long id);
    void delete(Long id);
    BillResponse generateBills(BillRequest request);
    void generateBillsForSelectedCustomers(List<Long> customerIds);
}
