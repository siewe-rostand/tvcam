package com.siewe_rostand.tvcam.Customers;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomersDTO {
    private Long id;

    @NotNull
    private String name;

    private String address;
    private String ref;

    @Min(9)
    private String telephone;

    private Boolean hasDebt;

    private Boolean hasPaid;

    private Boolean isActive;

    private Boolean isSuspended;

    public CustomersDTO CreateDTO(Customers customers) {
        if (customers != null) {
            CustomersDTO customersDto = new CustomersDTO();

            customersDto.setId(customers.getCustomerId());
            customersDto.setName(customers.getName());
            customersDto.setRef(customers.getRef());
            customersDto.setAddress(customers.getAddress());
            customersDto.setTelephone(customers.getTelephone());
            customersDto.setHasDebt(customers.getHasDebt());
            customersDto.setHasPaid(customers.getHasPaid());
            customersDto.setIsActive(customers.getIsActive());
            customersDto.setIsSuspended(customers.getIsSuspended());

            return customersDto;
        }
        return null;
    }
}
