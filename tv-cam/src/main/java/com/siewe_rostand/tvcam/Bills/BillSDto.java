package com.siewe_rostand.tvcam.Bills;

public record BillSDto (
         Long id,
         String month,
         String year,
         String depositDate,
         String deadline,
         Integer amount,
         Integer debt,
         Integer penalties,
         Integer netToPay,
         String observation,
         Long customerId
){
}
