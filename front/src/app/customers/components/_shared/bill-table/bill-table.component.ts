import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Button} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {MessageService, PrimeTemplate} from "primeng/api";
import {TableModule} from "primeng/table";
import {TagModule} from "primeng/tag";
import {BillModel} from "../../../model/bill.model";
import {PaymentStatusComponent} from "../payment-table/payment-status/payment-status.component";
import {DialogModule} from "primeng/dialog";
import {DividerModule} from "primeng/divider";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {PaymentModel} from "../../../model/payment.model";
import {PaymentService} from "../../../service/payment.service";

@Component({
  selector: 'app-bill-table',
  standalone: true,
  imports: [
    Button,
    InputTextModule,
    PrimeTemplate,
    TableModule,
    TagModule,
    PaymentStatusComponent,
    DialogModule,
    DividerModule,
    DropdownModule,
    FormsModule,
    NgIf
  ],
  templateUrl: './bill-table.component.html',
  styleUrl: './bill-table.component.css'
})
export class BillTableComponent {
  @Input() bills: BillModel[] = [];
  @Output() selectedBillsChange = new EventEmitter<BillModel[]>();

  selectedBills: BillModel[] = [];
  makePaymentDialog: boolean = false;
  bill: BillModel = {};
  amount: number = 0;
  submitted: boolean = false;
  commentaire: string  = '';
  paymentMethod: any[] = [{name: 'CASH', value:'CASH'},
    {name: 'MTN MONEY', value:'MTN_MONEY'},
    {name: 'ORANGE MONEY', value:'ORANGE_MONEY'}];
  selectedPaymentMethod: any | undefined;
  payment: PaymentModel= {};


  constructor(private paymentService: PaymentService, private messageService: MessageService) {
  }
  onSelectionChange(event: BillModel) {
    this.selectedBillsChange.emit(this.selectedBills);
  }

  openEdit(bill: BillModel) {
    this.bill = {...bill};
    this.makePaymentDialog = true;
    console.log(bill);
  }


  deleteBill(bill: BillModel) {

  }
  hideUpdateBill() {
    this.makePaymentDialog = false;
  }


  makePayment() {
    this.submitted = true;
    this.payment = {
      amount: this.amount,
      customerId: this.bill.customerId,
      billId: this.bill.id,
      observation: this.commentaire,
      paymentMethod: this.selectedPaymentMethod == undefined ? "CASH" : this.selectedPaymentMethod['value'],
    };
    console.log(this.payment)
    this.paymentService.makePayment(this.payment).subscribe({
      next: data => {
        this.submitted = false;
        console.log(data);
        this.messageService.add({
          severity: 'success',
          summary: 'Successful',
          detail: 'le paiement a été effectuée avec succès',
          life: 4000,
        });
      },
      error: err => {
        this.submitted = false;
        console.log(err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Une erreur s\'est produite lors de la suppression',
          life: 3000,
        });
      }
    })
  }
}
