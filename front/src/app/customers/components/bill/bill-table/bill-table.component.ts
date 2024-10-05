import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Button} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {ConfirmationService, MessageService} from "primeng/api";
import {TableModule} from "primeng/table";
import {TagModule} from "primeng/tag";
import {BillModel} from "../../../model/bill.model";
import {PaymentStatusComponent} from "../../_shared/payment-table/payment-status/payment-status.component";
import {DialogModule} from "primeng/dialog";
import {DividerModule} from "primeng/divider";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {PaymentModel} from "../../../model/payment.model";
import {PaymentService} from "../../../service/payment.service";
import {ToastModule} from "primeng/toast";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {BillService} from "../../../service/bill.service";

@Component({
  selector: 'app-bill-table',
  standalone: true,
  imports: [
    Button,
    InputTextModule,
    TableModule,
    TagModule,
    PaymentStatusComponent,
    DialogModule,
    DividerModule,
    DropdownModule,
    FormsModule,
    NgIf,
    ToastModule,
    ConfirmDialogModule
  ],
  templateUrl: './bill-table.component.html',
  styleUrl: './bill-table.component.css',
  providers: [MessageService, ConfirmationService],
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


  constructor(private paymentService: PaymentService, private billService: BillService,
              private messageService: MessageService, private confirmationService: ConfirmationService,) {
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
    this.confirmationService.confirm({
      message: `Êtes-vous sûr de vouloir supprimer la facture de <b>${bill.customerName?.toUpperCase()}</b>
      d'un montant <b>${bill.paidAmount?.toString().toUpperCase()}</b> et tous les paiements liee a cette facture?`,
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      dismissableMask: false,
      accept: () => {
        this.billService.deleteBill(bill.id!).subscribe({
          next: (res) => {
            console.log(res)
            this.messageService.add({
              severity: 'success',
              summary: 'Successful',
              detail: 'Facture supprimée avec succès',
              life: 3000,
            });
          }
        })

      },
    });
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
    })
  }
}
