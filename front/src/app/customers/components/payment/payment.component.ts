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
import {PaymentStatusComponent} from "../_shared/payment-table/payment-status/payment-status.component";
import {PaymentTableComponent} from "../_shared/payment-table/payment-table.component";

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
    PaymentStatusComponent,
    PaymentTableComponent
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

  getAllPayments() {
    this.paymentService.getMonthlyPayment('AUGUST').subscribe({
      next: (response) => {
        console.log(response.content)
        this.payments = response.content;
      },
      error: (error) => {
        console.log(error)
      }
    })
  }

}
