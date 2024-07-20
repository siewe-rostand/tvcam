package com.siewe_rostand.tvcam.Bills;

import com.siewe_rostand.tvcam.Customers.Customers;
import com.siewe_rostand.tvcam.Payment.PaymentStatus;
import com.siewe_rostand.tvcam.Payment.Payments;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bills")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Bills {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bills")
    List<Payments> payments;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long billId;
    private String month;
    private String year;
    @Column(name = "deposit_date")
    private String depositDate;
    private String deadline;
    private Integer amount;
    private Integer debt;
    private Integer penalties;
    @Column(name = "net_to_pay")
    private Integer netToPay;
    private String observation;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;
    @CreatedDate
    private LocalDateTime createdAt;

    @OneToOne()
    @JoinColumn(name = "customer_id")
    private Customers customers;

    public Bills toMap(BillSDto billSDto, Customers customers) {
        return Bills.builder().billId(billSDto.getId()).amount(billSDto.getAmount())
                .month(billSDto.getMonth()).year(billSDto.getYear()).netToPay(billSDto.getNetToPay())
                .debt(billSDto.getDebt()).deadline(billSDto.getDeadline()).penalties(billSDto.getPenalties())
                .observation(billSDto.getObservation()).depositDate(billSDto.getDepositDate()).customers(customers)
                .build();
    }
}
