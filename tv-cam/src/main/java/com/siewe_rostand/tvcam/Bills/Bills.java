package com.siewe_rostand.tvcam.Bills;

import com.siewe_rostand.tvcam.Customers.Customers;
import com.siewe_rostand.tvcam.Payment.PaymentFrequency;
import com.siewe_rostand.tvcam.Payment.PaymentStatus;
import com.siewe_rostand.tvcam.Payment.Payments;
import com.siewe_rostand.tvcam.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bills")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Bills extends BaseEntity {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bills")
    List<Payments> payments;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long billId;
    @NotNull(message = "need to specify the month of the bill")
    private String month;
    @NotNull(message = "Year is required")
    private String year;
    @Column(name = "deposit_date")
    private String depositDate;
    private String deadline;
    private BigDecimal debt;
    private Integer penalties;

    @NotNull(message = "bill amount must not be null")
    private BigDecimal paidAmount;
    @Column(name = "net_to_pay")
    private BigDecimal netToPay;
    private BigDecimal monthlyPayment;

    private String observation;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;
    private boolean currentPeriodBill;


    @ManyToOne()
    @JoinColumn(name = "customer_id")
    private Customers customers;

}
