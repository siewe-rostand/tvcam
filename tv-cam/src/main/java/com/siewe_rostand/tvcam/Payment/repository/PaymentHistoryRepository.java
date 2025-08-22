package com.siewe_rostand.tvcam.Payment.repository;

import com.siewe_rostand.tvcam.Customers.Customers;
import com.siewe_rostand.tvcam.Payment.Payments;
import com.siewe_rostand.tvcam.Payment.model.PaymentHistory;
import com.siewe_rostand.tvcam.Users.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for PaymentHistory entity
 * 
 * @author rostand
 * @project tv-cam
 */
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    
    List<PaymentHistory> findAllByCustomerOrderByCreatedAtDesc(Customers customer);
    
    List<PaymentHistory> findAllByCollectedByUserOrderByCreatedAtDesc(Users user);
    
    List<PaymentHistory> findAllByPayment(Payments payment);
    
    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.customer = :customer " +
           "ORDER BY ph.createdAt DESC")
    Page<PaymentHistory> findCustomerPaymentHistory(@Param("customer") Customers customer, Pageable pageable);
    
    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.collectedByUser = :user " +
           "ORDER BY ph.createdAt DESC")
    Page<PaymentHistory> findUserCollectionHistory(@Param("user") Users user, Pageable pageable);
    
    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.customer.zone.zoneId IN :zoneIds " +
           "ORDER BY ph.createdAt DESC")
    Page<PaymentHistory> findZonePaymentHistory(@Param("zoneIds") List<Long> zoneIds, Pageable pageable);
    
    @Query("SELECT COUNT(ph) FROM PaymentHistory ph WHERE ph.collectedByUser = :user")
    Long countByCollectedByUser(@Param("user") Users user);
    
    @Query("SELECT SUM(ph.payment.amount) FROM PaymentHistory ph WHERE ph.collectedByUser = :user")
    Double sumAmountByCollectedByUser(@Param("user") Users user);
}