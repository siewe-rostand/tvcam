package com.siewe_rostand.tvcam.Customers;


public class CustomerDTOMapper {
//    public CustomersDto CreateDTO(Customers customers){
//        return CustomersDto.builder().id(customers.getCustomerId()).name(customers.getName())
//                .address(customers.getAddress()).telephone(customers.getTelephone()).hasDebt(customers.getHasDebt()).hasPaid(customers.getHasPaid())
//                .isActive(customers.getIsActive()).isSuspended(customers.getIsSuspended()).build();
//    }

    public Customers toEntity(CustomersDTO customersDto){
        return Customers.builder().customerId(customersDto.getId()).name(customersDto.getName()).address(customersDto.getAddress())
                .hasPaid(customersDto.getHasPaid()).hasDebt(customersDto.getHasDebt()).telephone(customersDto.getTelephone()).isActive(customersDto.getIsActive())
                .isSuspended(customersDto.getIsSuspended()).build();
    }
}
