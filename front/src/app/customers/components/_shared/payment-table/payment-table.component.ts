import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Button} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {PrimeTemplate} from "primeng/api";
import {TableModule} from "primeng/table";
import {TagModule} from "primeng/tag";
import {PaymentStatusComponent} from "./payment-status/payment-status.component";
import {NgForOf, NgIf} from "@angular/common";
import {DialogModule} from "primeng/dialog";
import {DividerModule} from "primeng/divider";
import {DropdownModule} from "primeng/dropdown";
import {ReactiveFormsModule} from "@angular/forms";
import {PaymentModel} from "../../../model/payment.model";
import {
  CustomerPaymentFrequencyComponent
} from "../../customer-list/customer-payment-frequency/customer-payment-frequency.component";

@Component({
  selector: 'app-payment-table',
  standalone: true,
  imports: [
    Button,
    InputTextModule,
    PrimeTemplate,
    TableModule,
    TagModule,
    PaymentStatusComponent,
    NgForOf,
    DialogModule,
    DividerModule,
    DropdownModule,
    NgIf,
    ReactiveFormsModule,
    CustomerPaymentFrequencyComponent
  ],
  templateUrl: './payment-table.component.html',
  styleUrl: './payment-table.component.css'
})
export class PaymentTableComponent {
  @Input() payments: any[] = [];
  @Input() caption: string = 'Liste des Paiements';
  @Output() selectedPaymentsChange = new EventEmitter<any[]>();

  selectedPayments: any[] = [];
  detailDialog: boolean = false;
  payment: PaymentModel = {};

  onSelectionChange(event: any) {
    this.selectedPaymentsChange.emit(this.selectedPayments);
  }
  getSelectedPayment(){
    console.log(this.selectedPayments);
  }

  hideUpdateBill() {
    this.detailDialog = false;
  }

  openDetail(payment: PaymentModel) {
    this.payment = {...payment};
    this.detailDialog = true;
  }
}
