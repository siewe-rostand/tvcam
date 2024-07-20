package com.siewe_rostand.tvcam.Payment;

import com.siewe_rostand.tvcam.Bills.Bills;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "payments")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long paymentId;

    private Integer amount;

    private Integer debt;

    private String observation;

    private String paymentMethod;

    @Column(name = "net_to_pay")
    private Integer netToPay;

    private LocalDate paymentDate;

    @ManyToOne
    @JoinColumn(name = "bill_id", referencedColumnName = "id")
    Bills bills;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Payments payments = (Payments) o;
        return paymentId != null && Objects.equals(paymentId, payments.paymentId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
