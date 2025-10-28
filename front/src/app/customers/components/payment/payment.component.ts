import {Component, OnInit} from '@angular/core';
import {NavbarComponent} from "../../../_shared/components/navbar/navbar.component";
import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {TableModule} from "primeng/table";
import {PaymentModel} from "../../model/payment.model";
import {CommonModule} from "@angular/common";
import {PaymentService} from "../../service/payment.service";
import {TagModule} from "primeng/tag";
import {ToolbarModule} from "primeng/toolbar";
import {PaymentTableComponent} from "../_shared/payment-table/payment-table.component";
import {BillUtils} from "../../../_shared/utils/bill.utils";
import {DataTableComponent} from "../../../_shared/components/data-table/data-table.component";

@Component({
  selector: 'app-bill-payment',
  standalone: true,
  imports: [
    NavbarComponent,
    ButtonModule,
    InputTextModule,
    TableModule,
    CommonModule,
    TagModule,
    ToolbarModule,
    PaymentTableComponent,
    DataTableComponent
  ],
  templateUrl: './payment.component.html',
  styleUrl: './payment.component.css'
})
export class PaymentComponent implements OnInit {
  payment!: PaymentModel;
  payments!: PaymentModel[];
  selectedPayments!: PaymentModel[];
  currentDate = new Date();
  currentMonth = this.currentDate.getMonth() + 1;


  constructor(private paymentService: PaymentService) {
  }

  ngOnInit(): void {
    console.log(this.currentMonth);
    this.getAllPayments();
  }

  handleSelectedPaymentChange(newSelection: any[]): void {
    this.selectedPayments = newSelection;
    console.log('Selected payments updated:', this.selectedPayments);
  }

  getMonthLabel(monthValue?: string): string {
    return BillUtils.getMonthLabelFr(monthValue);
  }

  getMonthlyPayments(month: number) {
    this.paymentService.getMonthlyPayment(month).subscribe({
      next: (response) => {
        console.log(response.data)
        this.payments = response.data;
      },
      error: (error) => {
        console.log(error)
      }
    })
  }

  getAllPayments() {
    this.paymentService.getAllPayments().subscribe({
      next: (response) => {
        console.log(response.data)
        this.payments = response.data;
      },
      error: (error) => {
        console.log(error)
      }
    })
  }

}
