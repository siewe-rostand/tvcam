package com.siewe_rostand.tvcam.Payment;

import com.siewe_rostand.tvcam.Bills.Bills;
import com.siewe_rostand.tvcam.Users.Users;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "payments")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long paymentId;

    private BigDecimal amount;

    private String paymentRef;

    private String observation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private String paymentDate;

    private String updatedAt;

    private String createdAt;

    @CreatedBy
    @Column(nullable = false)
    private Long createdBy;

    @LastModifiedBy
    private Long modifiedBy;

    @ManyToOne
    @JoinColumn(name = "bill_id", referencedColumnName = "id")
    Bills bills;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    Users users;

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

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        createdAt = formatter.format(now);
        if (paymentMethod != null) {
            paymentMethod = PaymentMethod.CASH;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (paymentMethod != null) {
            paymentMethod = PaymentMethod.CASH;
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        updatedAt = formatter.format(now);
    }
}
