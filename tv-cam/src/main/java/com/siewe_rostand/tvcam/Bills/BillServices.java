package com.siewe_rostand.tvcam.Bills;

import com.siewe_rostand.tvcam.exceptions.ResourceNotFoundException;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;

/**
 * @author rostand
 * @project tv-cam
 */
public interface BillServices {
    Bills save(BillSDto billSDto) throws ResourceNotFoundException;
    Bills update(BillSDto billSDto);
    PaginatedResponse findAll(Integer page, Integer size, String sortBy, String direction, String name);
    BillSDto findById(Long id);
    void delete(Long id);
}
