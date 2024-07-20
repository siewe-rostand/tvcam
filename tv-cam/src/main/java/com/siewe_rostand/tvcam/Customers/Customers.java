package com.siewe_rostand.tvcam.Customers;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long customerId;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Size(max = 15, min = 9)
    @Column(name = "telephone")
    private String telephone;

    @Column(name = "has_debt")
    private Boolean hasDebt;

    @Column(name = "has_paid")
    private Boolean hasPaid;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_suspended")
    private Boolean isSuspended;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

//    @PrePersist
//    protected void onCreate() {
//        createdAt =  LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt =  LocalDateTime.now();
//    }

    public Customers toMap(CustomersDTO customersDto) {
        return Customers.builder().customerId(customersDto.getId()).name(customersDto.getName()).address(customersDto.getAddress())
                .hasPaid(customersDto.getHasPaid()).hasDebt(customersDto.getHasDebt()).telephone(customersDto.getTelephone()).isActive(customersDto.getIsActive())
                .isSuspended(customersDto.getIsSuspended()).build();
    }
}
