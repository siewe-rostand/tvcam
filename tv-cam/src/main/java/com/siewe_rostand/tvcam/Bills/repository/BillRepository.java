package com.siewe_rostand.tvcam.Bills.repository;

import com.siewe_rostand.tvcam.Bills.dto.BillSummary;
import com.siewe_rostand.tvcam.Bills.model.Bills;
import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

        // Optimized queries using BillSummary for better performance
        @Query("SELECT new com.siewe_rostand.tvcam.Bills.dto.BillSummary(b.billId, b.month, b.netToPay, c.name) " +
                        "FROM Bills b JOIN b.customers c WHERE c.customerId = :customerId")
        List<BillSummary> findBillSummariesByCustomerId(@Param("customerId") Long customerId);

        @Query("SELECT new com.siewe_rostand.tvcam.Bills.dto.BillSummary(b.billId, b.month, b.year, b.netToPay, b.paymentStatus, c.customerId, c.name) "
                        +
                        "FROM Bills b JOIN b.customers c WHERE b.currentPeriodBill = true")
        List<BillSummary> findCurrentPeriodBillSummaries();

        @Query("SELECT new com.siewe_rostand.tvcam.Bills.dto.BillSummary(b.billId, b.month, b.year, b.netToPay, b.paymentStatus, c.customerId, c.name) "
                        +
                        "FROM Bills b JOIN b.customers c WHERE b.paymentStatus = :paymentStatus")
        List<BillSummary> findBillSummariesByPaymentStatus(@Param("paymentStatus") PaymentStatus paymentStatus);

        @Query("SELECT new com.siewe_rostand.tvcam.Bills.dto.BillSummary(b.billId, b.month, b.year, b.netToPay, b.paymentStatus, c.customerId, c.name) "
                        +
                        "FROM Bills b JOIN b.customers c " +
                        "WHERE (b.month LIKE %:searchTerm% OR c.name LIKE %:searchTerm%) " +
                        "ORDER BY b.createdAt DESC")
        Page<BillSummary> findBillSummariesWithSearch(@Param("searchTerm") String searchTerm, Pageable pageable);

        // Keep existing full entity queries for when complete data is needed
        @Query("SELECT b FROM Bills b JOIN FETCH b.customers WHERE b.currentPeriodBill = true")
        List<Bills> findCurrentPeriodBillsWithCustomers();
}
