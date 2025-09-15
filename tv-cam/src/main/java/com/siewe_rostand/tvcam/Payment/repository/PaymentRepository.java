package com.siewe_rostand.tvcam.Payment.repository;

import com.siewe_rostand.tvcam.Payment.model.Payments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payments,Long> {
    @Query("select p from Payments p "
            + "where p.paymentRef like ?1 or p.paymentDate like ?1 or ?1 is null")
    Page<Payments> findAll(String name, Pageable pageable);

    List<Payments> findByBills_CustomersCustomerId(Long customerId);
    List<Payments> findByPaymentDate(String paymentDate);
    List<Payments> findByBills_Month(Integer month);
}
