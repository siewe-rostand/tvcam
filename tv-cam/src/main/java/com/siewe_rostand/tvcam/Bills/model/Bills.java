package com.siewe_rostand.tvcam.Bills.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Payment.model.Payments;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentStatus;
import com.siewe_rostand.tvcam.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "bills")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
public class Bills extends BaseEntity {
  @JsonIgnore
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "bills", fetch = FetchType.LAZY, orphanRemoval = true)
  List<Payments> payments;
  @Id
  @SequenceGenerator(name = "bills_seq", sequenceName = "bills_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bills_seq")
  @Column(name = "id")
  private Long billId;

  @NotNull(message = "need to specify the month of the bill")
  private Integer month;

  @NotNull(message = "Year is required")
  private Integer year;

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
