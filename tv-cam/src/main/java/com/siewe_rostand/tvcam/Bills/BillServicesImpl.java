package com.siewe_rostand.tvcam.Bills;

import com.siewe_rostand.tvcam.Customers.Customers;
import com.siewe_rostand.tvcam.Customers.CustomersRepository;
import com.siewe_rostand.tvcam.exceptions.ResourceNotFoundException;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import org.springframework.stereotype.Service;

/**
 * @author rostand
 * @project tv-cam
 */

@Service
public class BillServicesImpl implements BillServices {
    private final BillRepository billRepository;

    private final CustomersRepository customersRepository;

    public BillServicesImpl(BillRepository billRepository, CustomersRepository customersRepository) {
        this.billRepository = billRepository;
        this.customersRepository = customersRepository;
    }

    @Override
    public Bills save(BillSDto billSDto) throws ResourceNotFoundException{
        Customers customers = customersRepository.findById(billSDto.getCustomerId())
                .orElseThrow(()-> new ResourceNotFoundException("customer with id "+billSDto.getCustomerId()+ " not found"));
        Bills bills = new Bills().toMap(billSDto,customers);
        return billRepository.save(bills);
    }

    @Override
    public Bills update(BillSDto billSDto) {
        return null;
    }

    @Override
    public PaginatedResponse findAll(Integer page, Integer size, String sortBy, String direction, String name) {
        return null;
    }

    @Override
    public BillSDto findById(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
