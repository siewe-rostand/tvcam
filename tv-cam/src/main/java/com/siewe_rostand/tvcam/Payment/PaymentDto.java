package com.siewe_rostand.tvcam.Payment;

import lombok.Data;

@Data
public class PaymentDto {
    private Long id;

    private Integer amount;

    private Integer debt;

    private String observation;

    private Integer netToPay;

    public PaymentDto CreateDTO(Payments payments){

        PaymentDto paymentDto = new PaymentDto();

        if (payments != null){
            paymentDto.setId(payments.getPaymentId());
            paymentDto.setAmount(payments.getAmount());
            paymentDto.setDebt(payments.getDebt());
            paymentDto.setObservation(payments.getObservation());
            paymentDto.setNetToPay(payments.getNetToPay());
        }
        return  paymentDto;
    }
}
