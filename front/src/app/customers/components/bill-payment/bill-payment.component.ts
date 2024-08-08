import {Component, OnInit} from '@angular/core';
import {NavbarComponent} from "../../../_shared/components/navbar/navbar.component";
import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {TableModule} from "primeng/table";
import {PaymentModel} from "../../model/payment.model";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-bill-payment',
  standalone: true,
  imports: [
    NavbarComponent,
    ButtonModule,
    InputTextModule,
    TableModule,
    CommonModule
  ],
  templateUrl: './bill-payment.component.html',
  styleUrl: './bill-payment.component.css'
})
export class BillPaymentComponent implements OnInit{
  payment!: PaymentModel;
  payments!: PaymentModel[];
  selectedPayments!: PaymentModel[];
  constructor() {
  }
    ngOnInit(): void {
        throw new Error('Method not implemented.');
    }

  onSelectionChange(event: any) {
    this.selectedPayments = event;
  }
}
