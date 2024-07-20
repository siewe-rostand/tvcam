package com.siewe_rostand.tvcam.Bills;

import lombok.Data;

@Data
public class    BillSDto {

    private Long id;

    private String month;

    private String year;

    private  String depositDate;

    private String deadline;

    private Integer amount;

    private Integer debt;

    private Integer penalties;

    private Integer netToPay;

    private String observation;

    private Long customerId;

    public BillSDto CreateDTO(Bills bills){
        BillSDto billSDto = new BillSDto();

        if (bills != null){
            billSDto.setId(bills.getBillId());
            billSDto.setMonth(bills.getMonth());
            billSDto.setYear(bills.getYear());
            billSDto.setDepositDate(bills.getDepositDate());
            billSDto.setDeadline(bills.getDeadline());
            billSDto.setPenalties(bills.getPenalties());
            billSDto.setNetToPay(bills.getNetToPay());
            billSDto.setObservation(bills.getObservation());
            billSDto.setDebt(bills.getDebt());
            billSDto.setAmount(bills.getAmount());
            billSDto.setCustomerId(bills.getCustomers().getCustomerId());
        }

        return  billSDto;
    }
}
