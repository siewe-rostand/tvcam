package com.siewe_rostand.tvcam.Customers;

import com.siewe_rostand.tvcam.Payment.PaymentFrequency;
import com.siewe_rostand.tvcam.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Customers extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long customerId;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "ref")
    private String ref;

    @Min(9)
    @Column(name = "telephone", unique = true, nullable = false)
    private String telephone;

    @Column(name = "has_debt")
    private Boolean hasDebt;

    @Column(name = "has_paid")
    private Boolean hasPaid;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_suspended")
    private Boolean isSuspended;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'MONTHLY'")
    private PaymentFrequency paymentFrequency;;

    private LocalDate lastBillGenerationDate;


    public Customers toMap(CustomersDTO customersDto) {
        return Customers.builder().customerId(customersDto.getId()).name(customersDto.getName()).address(customersDto.getAddress())
                .hasPaid(customersDto.getHasPaid()).hasDebt(customersDto.getHasDebt()).telephone(customersDto.getTelephone()).isActive(customersDto.getIsActive())
                .isSuspended(customersDto.getIsSuspended()).build();
    }
}
