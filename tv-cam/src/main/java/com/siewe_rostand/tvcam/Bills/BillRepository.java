package com.siewe_rostand.tvcam.Bills;

import com.siewe_rostand.tvcam.Customers.Customers;
import com.siewe_rostand.tvcam.Payment.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bills, Long> {
    List<Bills> findAllByCustomersAndPaymentStatus(Customers customers, PaymentStatus paymentStatus);

    @Query("select b from Bills b "
            + "where b.month like ?1 or b.customers.name like ?1 or ?1 is null")
    Page<Bills> findAll(String name, Pageable pageable);

    @Modifying
    @Query("UPDATE Bills b SET b.currentPeriodBill = false")
    void resetCurrentPeriodFlag();

    Optional<Bills> findByCustomersAndCurrentPeriodBill(Customers customers, boolean currentPeriodBill);

    List<Bills> findAllByCustomers(Customers customers);
}
